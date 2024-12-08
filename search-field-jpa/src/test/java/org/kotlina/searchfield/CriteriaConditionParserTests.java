package org.kotlina.searchfield;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@DataJpaTest
@Sql("/search-field.sql")
public class CriteriaConditionParserTests {

    @Autowired
    EntityManager entityManager;

    @Test
    public void test001() {

        final String[][] testCases = new String[][] {
                new String[] { "a", "^a.*$" },
                new String[] { "*a*", ".*a.*" },
                new String[] { "*a", ".*a" },
                new String[] { "=a", "^a$" },
                new String[] { "=aaaaaaaaaaaaaa", "^aaaaaaaaaaaaaa$" },
                new String[] { "b & !b*c", "^b.*[^c]$" },
                new String[] { "*c & (a | b)", "[ab].*c$" },
                new String[] { "*c & a | b", "^a.*c|^b.*" },
                new String[] { "=\"cc&c\" | =\"dd|d\" | =\"ee!e\" | =\"with space\"", "^cc&c$|dd\\|d|ee!e|with space" },
                new String[] { "!*a*", "[^a].*" },
                new String[] { "中文", "中文.*" },
                new String[] { "=\"中文带空 格\"", "中文带空 格" },
                new String[] { "中文?中间", "^中文.中间.*" },
                new String[] { "中文??中间", "^中文..中间.*" }
        };

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        final CriteriaQuery<SearchEntity> query = criteriaBuilder.createQuery(SearchEntity.class);

        final Root<SearchEntity> root = query.from(SearchEntity.class);

        final Expression<String> fieldPath = root.get("title");

        final CriteriaConditionParser parser = new CriteriaConditionParser(fieldPath, criteriaBuilder);

        for (String[] a : testCases) {
            final String searchString = a[0];
            final String exceptPattern = a[1];

            final Predicate predicate = parser.parse(searchString);

            final List<SearchEntity> resultEntities = entityManager.createQuery(query.where(predicate)).getResultList();

            Assertions.assertNotEquals(0, resultEntities.size(), searchString + " " + exceptPattern);

            Assertions.assertTrue(
                    resultEntities.stream().allMatch(it -> it.getTitle().matches(exceptPattern)),
                    searchString + " " + exceptPattern
            );
        }
    }

    @SpringBootApplication
    public static class App {}
}
