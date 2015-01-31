package org.dd4t.core.processors;

/**
 * RunPhase
 *
 * Used to configure custom processors and the phase
 * in which they should run when the Factory classes go to
 * work.
 *
 * @author R. Kempees
 */
public enum RunPhase {
	BEFORE_CACHING, AFTER_CACHING, BOTH
}
