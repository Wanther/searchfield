# Search Field

## Introduction

Inspired by [Siebel Search](https://docs.oracle.com/cd/F26413_09/books/Search/filter-search-specifications-syntax-for-oracle-secure-enterprise-search.html#filter-search-specifications-syntax-for-oracle-secure-enterprise-search)

For Security Reason, it cannot specify Field by Frontend users, developer should set parsed result to specified field.

## Samples

@see tests

| Input         | Parsed Condition                            |
|---------------|---------------------------------------------|
| aa            | LIKE 'aa%'                                  |
| aa*           | LIKE 'aa%'                                  |
| *aa           | LIKE '%aa'                                  |
| \*aa\*        | LIKE '%aa%'                                 |
| aa?           | LIKE 'aa_'                                  |
| a?a           | LIKE 'a_a'                                  |
| =aa           | = 'aa'                                      |
| !aa           | NOT LIKE 'aa%'                              |
| !=aa          | != 'aa'                                     |
| aa & bb       | LIKE 'aa%' AND like 'bb%'                   |
| aa \| bb      | LIKE 'aa%' OR LIKE 'bb%'                    |
| aa & bb \| cc | LIKE 'aa%' AND LIKE 'bb%' OR LIKE 'cc%'     |
| aa & (bb \| cc) | LIKE 'aa%' AND (LIKE 'bb%' OR LIKE 'cc%')   |
| !aa & =bb \| != cc | != 'aa' AND = 'bb' OR != 'cc'               |
|!(aa && bb) | NOT (LIKE 'aa%' AND LIKE 'bb%')             |
| >= aa | >= 'aa' |
| < aa | < 'aa' |
| ... | ... |


## Usage

### Build from source
- Clone this repository
- ./gradlew build

```java
final CriteriaConditionParser parser = new CriteriaConditionParser(fieldPath, criteriaBuilder);
final Predicate predicate = parser.parse(searchString);

// ...
// query.where(predicate)
```

You can use parsed predicate in JPA `CriterialQuery`, `Specification<T>` .etc .

## Limitation
  - Only Support String Field Now

