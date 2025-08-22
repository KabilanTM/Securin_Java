const API = '/api/recipes';
let state = {
  page: 1,
  limit: 20,
  total: 0,
  filters: {
    title: '',
    cuisine: '',
    rating: '',
    total_time: '',
    calories: ''
  },
  cache: [],
  isLoading: false
};

const el = id => document.getElementById(id);
const tbody = el('recipesBody');

function showLoading() {
  state.isLoading = true;
  tbody.innerHTML = '<tr><td colspan="5" class="loading">Loading recipes...</td></tr>';
  el('prevBtn').disabled = true;
  el('nextBtn').disabled = true;
}

function hideLoading() {
  state.isLoading = false;
}

function showError(message) {
  tbody.innerHTML = `<tr><td colspan="5" class="error">${message}</td></tr>`;
  el('noResults').classList.add('hidden');
  el('noData').classList.add('hidden');
}

function drawStars(rating) {
  const val = Number(rating || 0);
  const full = Math.floor(val);
  const half = (val - full) >= 0.5 ? 1 : 0;
  const stars = 5;
  let s = '';
  for (let i=0;i<stars;i++) {
    s += `<span class="star ${i<full ? 'filled' : 'empty'}"></span>`;
  }
  return `<span class="rating" title="${val}">${s}</span>`;
}

async function fetchPage() {
  if (state.isLoading) return;
  
  const { page, limit, filters } = state;
  let url;
  const hasAny = Object.values(filters).some(v => v && v.trim() !== '');
  
  showLoading();
  
  try {
    if (hasAny) {
      const q = new URLSearchParams();
      if (filters.title) q.set('title', filters.title);
      if (filters.cuisine) q.set('cuisine', filters.cuisine);
      if (filters.rating) q.set('rating', filters.rating);
      if (filters.total_time) q.set('total_time', filters.total_time);
      if (filters.calories) q.set('calories', filters.calories);
      q.set('page', page);
      q.set('limit', limit);
      url = `${API}/search?${q.toString()}`;
    } else {
      url = `${API}?page=${page}&limit=${limit}`;
    }
    
    const res = await fetch(url);
    
    if (!res.ok) {
      throw new Error(`HTTP error! status: ${res.status}`);
    }
    
    const data = await res.json();
    
    state.total = data.total || 0;
    state.cache = data.data || [];
    renderTable(state.cache);
    renderPager();
    renderEmptyStates();
    
  } catch (error) {
    console.error('Error fetching recipes:', error);
    showError(`Failed to load recipes: ${error.message}. Please try again.`);
  } finally {
    hideLoading();
  }
}

function renderTable(rows) {
  tbody.innerHTML = '';
  rows.forEach(r => {
    const tr = document.createElement('tr');
    tr.className = 'row';
    tr.innerHTML = `
      <td><span class="title-cell" title="${r.title}">${r.title}</span></td>
      <td>${r.cuisine || ''}</td>
      <td>${drawStars(r.rating)}</td>
      <td>${r.total_time ?? ''}</td>
      <td>${r.serves || ''}</td>
    `;
    tr.addEventListener('click', () => openDrawer(r));
    tbody.appendChild(tr);
  });
}

function renderPager() {
  const { page, limit, total } = state;
  const pages = Math.max(1, Math.ceil(total / limit));
  el('pageInfo').textContent = `Page ${page} of ${pages} — ${total} results`;
  el('prevBtn').disabled = page <= 1;
  el('nextBtn').disabled = page >= pages;
}

function renderEmptyStates() {
  const hasRows = state.cache.length > 0;
  el('noData').classList.toggle('hidden', state.total !== 0);
  el('noResults').classList.toggle('hidden', hasRows || state.total === 0);
}

function openDrawer(r) {
  el('drawerTitle').textContent = r.title || '';
  el('drawerCuisine').textContent = r.cuisine || '';
  el('drawerDesc').textContent = r.description || '';
  el('totalTimeText').textContent = (r.total_time ?? '') + ' mins';
  el('prepTimeText').textContent = r.prep_time ?? '';
  el('cookTimeText').textContent = r.cook_time ?? '';

  const nutr = r.nutrients || {};
  const order = [
    'calories',
    'carbohydrateContent',
    'cholesterolContent',
    'fiberContent',
    'proteinContent',
    'saturatedFatContent',
    'sodiumContent',
    'sugarContent',
    'fatContent'
  ];
  const tbodyN = el('nutrBody');
  tbodyN.innerHTML = '';
  order.forEach(k => {
    if (nutr[k] !== undefined) {
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${k}</td><td>${nutr[k]}</td>`;
      tbodyN.appendChild(tr);
    }
  });

  el('timesDetails').classList.add('hidden');
  el('drawer').classList.add('open');
}

function closeDrawer() {
  el('drawer').classList.remove('open');
}

function wireEvents() {
  el('prevBtn').addEventListener('click', () => { state.page = Math.max(1, state.page - 1); fetchPage(); });
  el('nextBtn').addEventListener('click', () => { state.page = state.page + 1; fetchPage(); });
  el('closeDrawer').addEventListener('click', closeDrawer);
  el('expandTimes').addEventListener('click', () => el('timesDetails').classList.toggle('hidden'));
  el('pageSize').addEventListener('change', (e) => {
    state.limit = Number(e.target.value);
    state.page = 1;
    fetchPage();
  });
  el('resetFilters').addEventListener('click', () => {
    ['titleFilter','cuisineFilter','ratingFilter','totalTimeFilter','servesFilter'].forEach(id => el(id).value='');
    state.filters = { title:'', cuisine:'', rating:'', total_time:'', calories:'' };
    state.page = 1;
    fetchPage();
  });


  el('titleFilter').addEventListener('input', debounce(() => { state.filters.title = el('titleFilter').value.trim(); state.page=1; fetchPage(); }, 300));
  el('cuisineFilter').addEventListener('input', debounce(() => { state.filters.cuisine = el('cuisineFilter').value.trim(); state.page=1; fetchPage(); }, 300));
  el('ratingFilter').addEventListener('input', debounce(() => { state.filters.rating = el('ratingFilter').value.trim(); state.page=1; fetchPage(); }, 300));
  el('totalTimeFilter').addEventListener('input', debounce(() => { state.filters.total_time = el('totalTimeFilter').value.trim(); state.page=1; fetchPage(); }, 300));


  el('servesFilter').addEventListener('input', debounce(() => {
    const q = el('servesFilter').value.trim().toLowerCase();
    const filtered = (q ? state.cache.filter(r => (r.serves||'').toLowerCase().includes(q)) : state.cache);
    renderTable(filtered);
  }, 200));
}

function debounce(fn, t) {
  let h; return (...args) => { clearTimeout(h); h = setTimeout(() => fn(...args), t); };
}

window.addEventListener('DOMContentLoaded', () => {
  wireEvents();
  fetchPage();
});
