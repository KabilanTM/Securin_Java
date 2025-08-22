package com.example.recipes.repo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.example.recipes.entity.Recipe;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class RecipeSearchRepository {

    @PersistenceContext
    private EntityManager em;

    public static class SearchParams {
        public String title;          
        public String cuisine;        
        public String ratingExpr;     
        public String totalTimeExpr;  
        public String caloriesExpr;   
        public Integer page = 1;
        public Integer limit = 10;
    }

    public static class PagedResult {
        public long total;
        public List<Recipe> data;
    }

    public PagedResult search(SearchParams p) {
        StringBuilder sql = new StringBuilder("FROM recipes r WHERE 1=1 ");
        Map<String, Object> params = new HashMap<>();

        if (p.title != null && !p.title.isBlank()) {
            sql.append("AND LOWER(r.title) LIKE :title ");
            params.put("title", "%" + p.title.toLowerCase() + "%");
        }
        if (p.cuisine != null && !p.cuisine.isBlank()) {
            sql.append("AND LOWER(r.cuisine) = :cuisine ");
            params.put("cuisine", p.cuisine.toLowerCase());
        }
        if (p.ratingExpr != null && !p.ratingExpr.isBlank()) {
            var opVal = parseOpVal(p.ratingExpr);
            if (opVal != null) {
                sql.append("AND r.rating ").append(opVal.op).append(" :rating ");
                params.put("rating", opVal.value);
            }
        }
        if (p.totalTimeExpr != null && !p.totalTimeExpr.isBlank()) {
            var opVal = parseOpVal(p.totalTimeExpr);
            if (opVal != null) {
                sql.append("AND r.total_time ").append(opVal.op).append(" :tt ");
                params.put("tt", opVal.value.intValue());
            }
        }
        if (p.caloriesExpr != null && !p.caloriesExpr.isBlank()) {
            var opVal = parseOpVal(p.caloriesExpr);
            if (opVal != null) {
                sql.append("AND r.calories_num ").append(opVal.op).append(" :cal ");
                params.put("cal", opVal.value.intValue());
            }
        }

        // Count
        Query countQ = em.createNativeQuery("SELECT COUNT(*) " + sql);
        params.forEach(countQ::setParameter);
        Number total = ((Number) countQ.getSingleResult());

        String select = "SELECT r.* ";
        String order = " ORDER BY r.rating DESC, r.id ASC ";
        Query dataQ = em.createNativeQuery(select + sql + order, Recipe.class);
        params.forEach(dataQ::setParameter);

        int page = (p.page == null || p.page < 1) ? 1 : p.page;
        int limit = (p.limit == null || p.limit < 1) ? 10 : p.limit;
        int offset = (page - 1) * limit;
        dataQ.setFirstResult(offset);
        dataQ.setMaxResults(limit);

        PagedResult pr = new PagedResult();
        pr.total = total.longValue();
        pr.data = dataQ.getResultList();
        return pr;
    }

    private static class OpVal { String op; Double value; }
    private static OpVal parseOpVal(String expr) {
        String s = expr.trim().replaceAll("\\s+", "");
        String[] ops = {">=", "<=", ">", "<", "="};
        for (String op : ops) {
            if (s.startsWith(op)) {
                String num = s.substring(op.length());
                try {
                    double v = Double.parseDouble(num);
                    OpVal ov = new OpVal();
                    ov.op = op;
                    ov.value = v;
                    return ov;
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
   
        try {
            double v = Double.parseDouble(s);
            OpVal ov = new OpVal();
            ov.op = "=";
            ov.value = v;
            return ov;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
