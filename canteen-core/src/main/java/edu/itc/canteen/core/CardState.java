// CardState.java
package edu.itc.canteen.core;

/** Life cycle of a card. See {@link Card} for the allowed transitions. */
public enum CardState { ISSUED, ACTIVE, BLOCKED, LOST, REPLACED, EXPIRED, CLOSED }