package com.jnb.cashcard;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/*
    This is where we tap into the magic of Spring Data and its data repository pattern.
    CrudRepository is an interface supplied by Spring Data. When we extend it (or other sub-Interfaces of Spring Data's Repository),
    Spring Boot and Spring Data work together to automatically generate the CRUD methods that we need to interact with a database.
*/
interface CashCardRepository extends CrudRepository<CashCard, Long>, PagingAndSortingRepository<CashCard, Long> {
    CashCard findByIdAndOwner(Long id, String owner);

    boolean existsByIdAndOwner(Long id, String owner);
    Page<CashCard> findByOwner(String owner, PageRequest pageRequest);
}
