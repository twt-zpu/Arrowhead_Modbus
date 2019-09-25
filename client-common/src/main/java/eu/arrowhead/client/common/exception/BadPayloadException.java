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
 * Thrown if a resource receives a HTTP payload which have missing mandatory fields.
 */
public class BadPayloadException extends ArrowheadException {

  public BadPayloadException(final String msg, final int errorCode, final String origin, final Throwable cause) {
    super(msg, errorCode, origin, cause);
    this.setExceptionType(ExceptionType.BAD_PAYLOAD);
  }

  public BadPayloadException(final String msg, final int errorCode, final String origin) {
    super(msg, errorCode, origin);
    this.setExceptionType(ExceptionType.BAD_PAYLOAD);
  }

  public BadPayloadException(String msg, int errorCode, Throwable cause) {
    super(msg, errorCode, cause);
    this.setExceptionType(ExceptionType.BAD_PAYLOAD);
  }

  public BadPayloadException(String msg, int errorCode) {
    super(msg, errorCode);
    this.setExceptionType(ExceptionType.BAD_PAYLOAD);
  }

  public BadPayloadException(String msg, Throwable cause) {
    super(msg, cause);
    this.setExceptionType(ExceptionType.BAD_PAYLOAD);
  }

  public BadPayloadException(String msg) {
    super(msg);
    this.setExceptionType(ExceptionType.BAD_PAYLOAD);
  }

}
