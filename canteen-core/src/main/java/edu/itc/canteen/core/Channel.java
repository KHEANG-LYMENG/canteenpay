// Channel.java
package edu.itc.canteen.core;

/** Where a top-up comes from. Each channel has its own fee rule in {@link TopUpPolicy}. */
public enum Channel { KHQR, CASH_DESK, PARENT_APP }