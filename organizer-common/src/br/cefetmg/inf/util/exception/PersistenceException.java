/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.cefetmg.inf.util.exception;

/**
 *
 * @author 7
 */
public class PersistenceException extends Exception {

    public PersistenceException() {
    }


    public PersistenceException(String msg) {
        super(msg);
    }

    public PersistenceException(String msg, Exception exception) {
        super(msg, exception);
    }
}
