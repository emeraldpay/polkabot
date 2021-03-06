package io.emeraldpay.moonbeam.processing

import io.emeraldpay.moonbeam.state.Agent
import spock.lang.Specification

class AgentParserSpec extends Specification {

    def "Extract standard"() {
        setup:
        def parser = new AgentParser()

        expect:
        def act = parser.parse(full)
        act.software == software
        act.version == version
        act.platform == platform

        where:
        full                                                            | software           | version   | platform
        "parity-polkadot/v0.7.19-d12575b9-x86_64-linux-gnu (unknown)"   | "parity-polkadot"  | "v0.7.19" | "linux"
        "plasm-node/v0.7.1-b4cea7d-x86_64-linux-gnu (unknown)"          | "plasm-node"       | "v0.7.1"  | "linux"
        "darwinia-node/1.0.0-fdf0687-x86_64-macos (unknown)"            | "darwinia-node"    | "v1.0.0"  | "darwin"
        "Edgeware Node/v3.0.1-x86_64-linux-gnu (unknown)"               | "Edgeware Node"    | "v3.0.1"  | "linux"
    }

    def "Extract non standard"() {
        setup:
        def parser = new AgentParser()

        expect:
        def act = parser.parse(full)
        act.software == software
        act.version == version
        act.platform == platform

        where:
        full                                                            | software           | version   | platform
        "Edgeware Node/v3.0.1-x86_64-linux-gnu (unknown)"               | "Edgeware Node"    | "v3.0.1"  | "linux"
    }

    def "Extract standard version"() {
        setup:
        def parser = new AgentParser()

        expect:
        def agent = new Agent("")
        parser.extractVersion(agent, full)
        agent.version == vesion
        agent.commit == commit
        agent.platformFull == platformFull

        where:
        full                                    | vesion    | commit     | platformFull
        "v0.7.19-d12575b9-x86_64-linux-gnu"     | "v0.7.19" | "d12575b9" | "x86_64-linux-gnu"
        "v0.7.1-b4cea7d-x86_64-linux-gnu"       | "v0.7.1"  | "b4cea7d"  | "x86_64-linux-gnu"
    }

    def "Extract version without commit"() {
        setup:
        def parser = new AgentParser()

        expect:
        def agent = new Agent("")
        parser.extractVersion(agent, full)
        agent.version == vesion
        agent.commit == commit
        agent.platformFull == platformFull

        where:
        full                                    | vesion    | commit     | platformFull
        "v3.0.1-x86_64-linux-gnu"               | "v3.0.1"  | null       | "x86_64-linux-gnu"
    }
}
