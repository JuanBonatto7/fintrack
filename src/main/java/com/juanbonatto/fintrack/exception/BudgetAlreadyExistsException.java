package com.juanbonatto.fintrack.exception;

public class BudgetAlreadyExistsException extends RuntimeException {

    public BudgetAlreadyExistsException() {
        super("Ya existe un presupuesto para esa cuenta, categoría y mes");
    }
}
