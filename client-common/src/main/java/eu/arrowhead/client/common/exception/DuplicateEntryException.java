/*
 *  Copyright (c) 2018 AITIA International Inc.
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.client.common.exception;

/**
 * Used by the <i>DatabaseManager</i> class when Hibernate <i>ConstraintViolationException</i>s happen. This can happen when trying to save a new
 * object with the same unique constraint fields as an already existing entry, or when trying to delete an entry which has foreign key constraints in
 * other tables.
 */
public class DuplicateEntryException extends ArrowheadException {

  public DuplicateEntryException(final String msg, final int errorCode, final String origin, final Throwable cause) {
    super(msg, errorCode, origin, cause);
    this.setExceptionType(ExceptionType.DUPLICATE_ENTRY);
  }

  public DuplicateEntryException(final String msg, final int errorCode, final String origin) {
    super(msg, errorCode, origin);
    this.setExceptionType(ExceptionType.DUPLICATE_ENTRY);
  }

  public DuplicateEntryException(String msg, int errorCode, Throwable cause) {
    super(msg, errorCode, cause);
    this.setExceptionType(ExceptionType.DUPLICATE_ENTRY);
  }

  public DuplicateEntryException(String msg, int errorCode) {
    super(msg, errorCode);
    this.setExceptionType(ExceptionType.DUPLICATE_ENTRY);
  }

  public DuplicateEntryException(String msg, Throwable cause) {
    super(msg, cause);
    this.setExceptionType(ExceptionType.DUPLICATE_ENTRY);
  }

  public DuplicateEntryException(String msg) {
    super(msg);
    this.setExceptionType(ExceptionType.DUPLICATE_ENTRY);
  }

}
