package org.kernet

class HelloSpock extends spock.lang.Specification {
    def "Hello world test to ensure Spock is working"() {
        expect:
        name.size() == length

        where:
        name     | length
        "Spock"  | 5
        "Kirk"   | 4
        "Scotty" | 6
    }
}  