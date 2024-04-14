package com.jnb.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;

import java.security.Principal;
import java.net.URI;
import java.util.*;

@RestController
// This tells Spring that this class is a Component of type RestController and capable of handling HTTP requests.
@RequestMapping("/cashcards")
// This is a companion to @RestController that indicates which address requests must have to access this Controller.
class CashCardController {

    private final CashCardRepository cashCardRepository;

    private CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }


    private CashCard findCashCard(Long requestedId, Principal principal) {
        return cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        CashCard cashCard = findCashCard(requestedId, principal);
        Optional<CashCard> cashCardOptional = Optional.ofNullable(findCashCard(requestedId, principal));
        if (cashCardOptional.isPresent()){
            return ResponseEntity.ok(cashCardOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    /*
     * @GetMapping marks a method as a handler method.
     * GET requests that match cashcards/{requestedID} will be handled by this method.
     */

    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb, Principal principal) {
        CashCard cashCardWithOwner = new CashCard(null, newCashCardRequest.amount(), principal.getName());
        CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);
        //  Instead of create a SQL script a new java method, Spring gives to you his own method. Amazing.
        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
        ));
        return ResponseEntity.ok(page.getContent());
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putCashCard(@PathVariable Long requestedId, @RequestBody CashCard cashCardUpdate, Principal principal) {

        CashCard cashCard = findCashCard(requestedId, principal);
        if (cashCard != null) {
            CashCard updatedCashCard = new CashCard(cashCard.id(), cashCardUpdate.amount(), principal.getName());
            cashCardRepository.save(updatedCashCard);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(
            @PathVariable Long id,
            Principal principal
    ) {
        if (!cashCardRepository.existsByIdAndOwner(id, principal.getName())) {
            return ResponseEntity.notFound().build();
        }
        cashCardRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
