package org.lpro.boundary.commande;

public class CommandeNotFound extends RuntimeException {
    public CommandeNotFound(String s) {
        super(s);
    }
}
