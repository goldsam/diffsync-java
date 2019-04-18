package org.github.goldsam.diffsync.core

import spock.lang.*

public class HostSpec extends Specification {
  def "computing the maximum of two numbers"() {
    expect:
    Math.max(a, b) == c

    where:
    a << [5, 3]
    b << [1, 9]
    c << [5, 9]
  }
  
}