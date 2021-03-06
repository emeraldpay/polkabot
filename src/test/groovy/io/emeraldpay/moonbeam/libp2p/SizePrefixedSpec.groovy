package io.emeraldpay.moonbeam.libp2p

import io.emeraldpay.moonbeam.ByteBufferCommons
import org.apache.commons.codec.binary.Hex
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import spock.lang.Specification

import java.nio.ByteBuffer
import java.time.Duration

class SizePrefixedSpec extends Specification{

    def "Standard - One packet split into 2"() {
        setup:
        def converter = SizePrefixed.Standard()
        def p1 = Hex.decodeHex(
                "00000070" + "0a10790190cd04bb6ba49dfb0b81a1e34ba0122408011220c121ecd16f069680341b54488f345a4905d111a262588517a23badfa33ec9bc91a0b502d3235362c502d333834221a4145532d3132382c4145532d3235362c54776f666973684354522a0d5348413235362c534841353132" +
                "00000085" + "0a41047caf3f33215b08eb11ac7b610e40985d383e39d09ea5cc093d8afde7469da78927882342b45f6706ccacdfa776b7539af929f4b9eecf2441749e64f00cf6f915124065bee0c1b4da7b38227b5e3b6bd9fb79ad8fb2eef78f98126dce32cb3d313c3664207d0086c19dc26c6c721535f0ca50fae13a92eafa04e6ab40ea9f391b0409"
        )

        when:
        def act = Flux.just(p1)
            .map { ByteBuffer.wrap(it) }
            .transform(converter.reader())
                .map { ByteBuffer buf ->
                    return Hex.encodeHexString(buf.array())
                }

        then:
        StepVerifier.create(act)
            .expectNext("0a10790190cd04bb6ba49dfb0b81a1e34ba0122408011220c121ecd16f069680341b54488f345a4905d111a262588517a23badfa33ec9bc91a0b502d3235362c502d333834221a4145532d3132382c4145532d3235362c54776f666973684354522a0d5348413235362c534841353132")
            .expectNext("0a41047caf3f33215b08eb11ac7b610e40985d383e39d09ea5cc093d8afde7469da78927882342b45f6706ccacdfa776b7539af929f4b9eecf2441749e64f00cf6f915124065bee0c1b4da7b38227b5e3b6bd9fb79ad8fb2eef78f98126dce32cb3d313c3664207d0086c19dc26c6c721535f0ca50fae13a92eafa04e6ab40ea9f391b0409")
            .expectComplete()
            .verify(Duration.ofSeconds(1))
    }

    def "Varint - Join 2 packets"() {
        setup:
        def converter = SizePrefixed.Varint()
        def p1 = Hex.decodeHex("e71b" + "0804500942c6020a221220c287ddd01e60471871de5baf4f0d3de490d22e89a0600bbaeb1b7249e97de70412080423f6d20b06767d120a0423f6d20b06767edd03120a047f00000106767edd031208047f00000106767d120a040a0b001906767edd031208040a0b001906767d120a040a01012706767edd031208040a01012706767d1216290000000000000000000000000000000106767edd031214290000000000000000000000000000000106767d1208040a9c012d06767d120a040a9c012d06767edd031208040a9c013306767d120a040a9c013306767edd031208040a9c013206767d120a040a9c013206767edd031208040a00018606767d120a040a00018606767edd031208040a0a0a0106767d120a040a0a0a0106767edd031208040a9c013d06767d120a040a9c013d06767edd031208040a9c012f06767d120a040a9c012f06767edd031801428e010a221220598f9330cf4ec4055094a74e145e449b0e6b5234c3ddc3f0d766a39fa48c6f811208048ac953ce06767d1208047f00000106767d120804ac11000106767d120804ac1c000106767d120804ac111d0306767d120804a9fe7f9c06767d1214290000000000000000000000000000000106767d1214292a0104f801721f8d000000000000000206767d180142700a221220b2ffb0dcf10b3c2c106858e62b52ddef5ddae0231f0d4d38a641d3f69e564dec120804b9aca50306767d1208047f00000106767d120804ac11000106767d1214290000000000000000000000000000000106767d1214290000000000000000000000000000000206767d1801426c0a22122037b731c7da28aac748c95656c824ee0baad5bf7dfa560d0588672310d671bdeb12080422f7bad706767d1208047f00000106767d1208040a0f023b06767d1208040a14000706767d1214290000000000000000000000000000000106767d1208040a0a0a0106767d425a0a221220e5b02f4fb7e40ff3553b77d9c9ed9b22b4c769666c6c5a2d9021de726a5eb01f120804b01f7e5c06767d1208047f00000106767d1208040a48d20106767d1214290000000000000000000000000000000106767d180142de020a22122001bf0946c650df9f79f90b7b63964f7455f1b1ad310656b7e6220f3a3a87339e1208043274339006767d1208040a0001e106767d1208040a0001c906767d12142900000000000000000000ffff3274339006767d1208040a0c04d106767d1208040a0c027706767d1208040a0c042b06767d120804ac11000106767d1208040a0a0a0106767d1208040a0001ad06767d1208040a00012506767d1208040a34031606767d1208040a34002906767d1208040a34002b06767d1208040a34003606767d1208040a00012d06767d1208040a0001ed06767d1208040a34004c")
        def p2 = Hex.decodeHex("800806767d1208040a0001b106767d1208040a34006706767d1208040a34007806767d1208040a34008506767d1208040a34009a06767d1208040a34009c06767d1208040a3400a706767d1208040a3400ae06767d1208040a3400d606767d1208040a3400eb06767d1208040a3400f706767d1208040a00011e06767d1801426e0a221220004add2fc511517b195f4ea38986278ab65cedd615f98cff53f38868f69c840d120804330f61f00687071208047f0000010687071208040a132c3b068707121429000000000000000000000000000000010687071208040a9c01330687071208040a2c06010687071801426e0a2212207e47ad66b38cbda133d61381ffa425f0dd1bd1421adb7d52a415a0f154ff2f5212080412b083bb06767d1208047f00000106767d120804ac1f11a006767d120804ac11000106767d1214290000000000000000000000000000000106767d1208040a9c013206767d1801425a0a2212201c6e41a6187d0976743fa96484ff7b7da577901f8ade5dd8442d6b96612e29ec1208046c3dd14906767d1208040a2c070106767d1208047f00000106767d1214290000000000000000000000000000000106767d180142640a2212203472022fad489b426190ba7cb6116e01aaf72cfba6c4b2f2245aa0d847a3f4331208042e04042406767d1208047f00000106767d120804c0a8000b06767d1214290000000000000000000000000000000106767d1208040a9c013a06767d180142620a2212204a48a548f89e488cf19fb886e68a71d1e74005667248904580e80ce73f0f0aeb1208048a444af206767d1208047f00000106767d1208040a13000506767d120804ac11000106767d1214290000000000000000000000000000000106767d42640a2212204ce5155a4311c8e413262acbf7fd881c0e2f7d5dc93812ea34d04c99cc8176aa12080423ccafa706767d1208040a0f000106767d1208047f00000106767d1208040ac8005806767d1214290000000000000000000000000000000106767d180142660a221220264d195479e4d2a32b16aadefc49ed17368125aad0b74096a09ac242f30b3b06120804951c73fd06767d1208047f00000106767d1214290000000000000000000000000000000106767d121429200119f05c01149d540002fffe42aa4b06767d180142560a2212201db47b8d3c0657e1134aaa8a14688ab4b8d2fa8ce25909fc284d75d5e84d6b2f12080422f3f46406767d1208047f00000106767d1208046460090e06767d1208040a0a0a0106767d1208040a9c013a06767d42e8020a221220a316fa96dbb312b62d122d5071a30ae76f37ff19aff3874f4da0766b70e0c18a120804b935810c064efe12142900000000000000000000ffffb935810c064efe1208040a0a0a01064efe1208040a0001c93e800888064efe1208040a9c012d064efe1208040a9c012f064efe1208040a9c012b064efe1208040a9c0133064efe1208040a9c0132064efe1208040a0000d4064efe1208040a0001ef064efe1208040a0001b1064efe1208040a0c042b064efe1208040a2c0301064efe1208040a2c0401064efe1208040a000309064efe1208040a00011e064efe1208040a2c0001064efe120804c0a80141064efe1208040a0c0277064efe1208040a0001ed064efe120804c0a80142064efe120804ac110001064efe1208040a0c04d1064efe1208040a2c0201064efe1208048ac545e8064efe1208040a2c0101064efe120804cebdd131064efe1208040af402a8064efe1208040a9c013d064efe1208040a9c013f064efe180142de020a221220d061d9734df0c1ba04c5d83baf7d14676a18ba5479b08606446d13e3b2d6cb9912142900000000000000000000ffff225c80fe06767d120804225c80fe06767d1208040a00011e06767d1208040a0001b106767d1208040a0a0a0106767d1208040a0c042b06767d1208040a0c027706767d1208040a00030606767d1208040a00030906767d1208040a34001c06767d1208040a34003106767d120804c0a8014106767d1208040a0c04d106767d1208040a34007406767d1208040a34007606767d1208040a34000906767d120804c0a87f6f06767d1208040a00030706767d120804c0a8014206767d1208040a00012706767d1208040a00018806767d1208040a0001ef06767d1208040a00014106767d1208040a0000d406767d1208040a2c000106767d1208040a9c012d06767d1208040a9c012b06767d1208040a9c012f06767d1208040a2c010106767d1208040a2c020106767d180142700a2212209da2677bcbae5cc46128200dae94fb5018881e83d73ff1e5b9c84103f6f3865212080474cbb5a106767d1208047f00000106767d120804ac11000106767d1214290000000000000000000000000000000106767d1214292a0104f80c2c771d000000000000000106767d1801426e0a221220968342dff8f96649a4ae151a211f0d3f692d487247328883f8a84ec1dec97c6e120804037a73fe06767d1208047f00000106767d120804ac1a27ea06767d1208040a00002206767d1214290000000000000000000000000000000106767d1208040a0a0a0106767d180142de020a221220963879fb986b41b1cdc95d0270e929c222cd0ed40bb979f41bb4d99ebc48395a12080468d330f706769b12142900000000000000000000ffff68d330f706769b1208040a01012a06769b120804ac11000106769b120804c0a8a10b06769b1208040a0001e106769b1208040a0c042b06769b1208040a0001b206769b1208040a00000606769b1208040a0c027706769b1208040af4010406769b1208040a0a0a0106c93ee903769b1208040a34001206769b1208040a0c04d106769b1208040a0001c906769b1208040a00018f06769b1208040a00018106769b1208040a0001d406769b1208040a0001f906769b1208040a00014d06769b1208040a0001ad06769b1208040a0000e806769b1208040a00012506769b1208040a34031606769b1208040a34002d06769b1208040a0001ed06769b1208040a34003a06769b1208040a00012d06769b1208040a0001b106769b1208040a34009d06769b180142ae020a221220b12ff18f3ce9d2c261d3960e1d91dfaa5b56b40c5074ee97842918b63712406112080423f6e05b06767d120a0423f6e05b06767edd03120a040a01011e06767edd031208040a01011e06767d120a047f00000106767edd031208047f00000106767d120a040a0b001806767edd031208040a0b001806767d1216290000000000000000000000000000000106767edd031214290000000000000000000000000000000106767d1208040a9c013a06767d120a040a9c013a06767edd031208040a9c013f06767d120a040a9c013f06767edd031208040a9c013b06767d120a040a9c013b06767edd031208040a9c013d06767d120a040a9c013d06767edd031208040a9c013206767d120a040a9c013206767edd03120a040a2c070106767edd03" + "1208040a2c070106767d")

        when:
        def act = Flux
                .fromIterable([p1, p2])
                .map { ByteBuffer.wrap(it) }
                .transform(converter.reader())

        then:
        StepVerifier.create(act)
                .assertNext { ByteBuffer it ->
                    def str = Hex.encodeHexString(it.array())

                    assert str.startsWith("08045009")
                    assert str.endsWith("767edd03")
                    assert it.remaining() == (p1.length + p2.length - 2 - 10) //TODO what goes after???
                }.as("Read main block")
                .expectNextCount(1).as("Read tail") // TODO 9 bytes of something
                .expectComplete()
                .verify(Duration.ofSeconds(1))
    }

    def "Varint - Read identity"() {
        setup:
        def converter = SizePrefixed.Varint()
        // multistream
        def p1 = Hex.decodeHex("14132f6d756c746973747265616d2f312e302e300a")
        // id header + protobuf
        def p2 = Hex.decodeHex("100f2f697066732f69642f312e302e300ac93e02dd02c93edd022a0e2f7375627374726174652f312e3032367375627374726174652d6e6f64652f76322e302e302d6263383564333532362d7838365f36342d6d61636f732028756e6b6e6f776e290a2408011220a7b29701a36c00368dbf2776afb15c51be02cc1ff758129be21fe8f093a1b8b6120804b99f9d0806767d1208040a00018806767d1208040a00018606767d1208047f00000106767d1214290000000000000000000000000000000106767d120804c0a8016e06767d120804c0a8c80106767d120804c0a8630106767d1208040a06077f06767d2208047f00000106d4cd1a112f7375627374726174652f666972352f361a112f7375627374726174652f666972352f351a112f7375627374726174652f666972352f341a112f7375627374726174652f666972352f331a102f697066732f70696e672f312e302e301a0e2f697066732f69642f312e302e301a0f2f697066732f6b61642f312e302e30")

        when:
        def act = Flux
                .fromIterable([p1, p2])
                .map { ByteBuffer.wrap(it) }
                .transform(converter.reader())
                .map { ByteBuffer buf ->
                    return Hex.encodeHexString(buf.array())
                }

        then:
        StepVerifier.create(act)
                // 14 - outer length
                .expectNext("13" + Hex.encodeHexString("/multistream/1.0.0\n".bytes)).as("Multistream header")
                // 10 - outer length
                .expectNext("0f" + Hex.encodeHexString("/ipfs/id/1.0.0\n".bytes)).as("Id header")
                // c93e - length
                .expectNext("02dd02c93edd022a0e2f7375627374726174652f312e3032367375627374726174652d6e6f64652f76322e302e302d6263383564333532362d7838365f36342d6d61636f732028756e6b6e6f776e290a2408011220a7b29701a36c00368dbf2776afb15c51be02cc1ff758129be21fe8f093a1b8b6120804b99f9d0806767d1208040a00018806767d1208040a00018606767d1208047f00000106767d1214290000000000000000000000000000000106767d120804c0a8016e06767d120804c0a8c80106767d120804c0a8630106767d1208040a06077f06767d2208047f00000106d4cd1a112f7375627374726174652f666972352f361a112f7375627374726174652f666972352f351a112f7375627374726174652f666972352f341a112f7375627374726174652f666972352f331a102f697066732f70696e672f312e302e301a0e2f697066732f69642f312e302e301a0f2f697066732f6b61642f312e302e30")
                .expectComplete()
                .verify(Duration.ofSeconds(1))
    }

    def "Varint - expects length to be finished"() {
        setup:
        def converter = SizePrefixed.Varint()
        def data = new byte[1000]
        def len = converter.prefix.write(data.size())
        assert len.remaining() > 1
        def p1 = ByteBuffer.wrap(len.array(),0, 1)
        def p2 = ByteBufferCommons.join(
                ByteBuffer.wrap(len.array(),1, len.remaining() - 1),
                ByteBuffer.wrap(data)
        )

        when:
        def act = converter.scanForExpected(p1.slice())
        then:
        act == 2

        when:
        act = converter.scanForExpected(ByteBufferCommons.join(p1, p2))
        then:
        act == 0
    }

    def "Varint - length prefix split into two packets"() {
        setup:
        def converter = SizePrefixed.Varint()
        def data = new byte[1000]
        def len = converter.prefix.write(data.size())
        assert len.remaining() > 1
        def p1 = ByteBuffer.wrap(len.array(), 0, 1)
        def p2 = ByteBufferCommons.join(
                ByteBuffer.wrap(len.array(),1, len.remaining() - 1),
                ByteBuffer.wrap(data)
        )

        when:
        def act = Flux.fromIterable([p1, p2])
                .transform(converter.reader())
                .map {
                    return Hex.encodeHexString(it.array())
                }

        then:
        StepVerifier.create(act)
            .expectNext("00" * 1000)
            .expectComplete()
            .verify(Duration.ofSeconds(1))
    }

    def "Varint - length prefix split into 3 packets"() {
        setup:
        def converter = SizePrefixed.Varint()
        def data = new byte[1000]
        def len = converter.prefix.write(data.size())
        assert len.remaining() > 1
        def p1 = ByteBuffer.wrap(len.array(), 0, 1)
        def p2 = ByteBuffer.wrap(len.array(), 1, len.remaining() - 1)
        def p3 = ByteBuffer.wrap(data)

        when:
        def act = Flux.fromIterable([p1, p2, p3])
                .transform(converter.reader())
                .map {
                    return Hex.encodeHexString(it.array())
                }

        then:
        StepVerifier.create(act)
                .expectNext("00" * 1000)
                .expectComplete()
                .verify(Duration.ofSeconds(1))
    }

    def "Standard - expects length to be finished"() {
        setup:
        def converter = SizePrefixed.Standard()
        def data = new byte[10]
        def len = converter.prefix.write(data.size())
        assert len.remaining() > 1
        def p1 = ByteBuffer.wrap(len.array(), 0, 2)
        def p2 = ByteBufferCommons.join(
                ByteBuffer.wrap(len.array(), 2, len.remaining() - 2),
                ByteBuffer.wrap(data)
        )

        when:
        def act = converter.scanForExpected(p1.slice())
        then:
        act > 0

        when:
        act = converter.scanForExpected(ByteBufferCommons.join(p1, p2))
        then:
        act == 0
    }

    def "Standard - length prefix split into two packets"() {
        setup:
        def converter = SizePrefixed.Standard()
        def data = new byte[100]
        def len = converter.prefix.write(data.size())
        assert len.remaining() > 1
        def p1 = ByteBuffer.wrap(len.array(), 0, 1)
        def p2 = ByteBufferCommons.join(
                ByteBuffer.wrap(len.array(), 1, len.remaining() - 1),
                ByteBuffer.wrap(data)
        )

        when:
        def act = Flux.fromIterable([p1, p2])
                .transform(converter.reader())
                .map {
                    return Hex.encodeHexString(it.array())
                }

        then:
        StepVerifier.create(act)
                .expectNext("00" * 100)
                .expectComplete()
                .verify(Duration.ofSeconds(1))
    }

    def "Standard - read value"() {
        setup:
        def prefix = SizePrefixed.Standard().prefix
        expect:
        prefix.read(ByteBuffer.wrap(Hex.decodeHex(hex))) == value
        where:
        hex         | value
        "00000000"  | 0
        "00000001"  | 1
        "000000ff"  | 255
        "00000100"  | 256
        "0000f000"  | 61440
        "12345678"  | 305419896
    }

    def "2 byte - read packet"() {
        setup:
        def converter = SizePrefixed.TwoBytes()
        def p1 = Hex.decodeHex(
                "00aa" + "23ea79402c915aa1624e2f0fcccdfb29857581a971b060ee39137b7bd2e5862f09642df91e163bd10f8bd6ac8ccab7b57b3fa02d23709104dc1db91e509b1a2800680a24080112202a7f5034210041fbeaae933bbb992aac05ae3163c5f32867142729df9405c0bb1240cec8f7055a59f7a2238de5db4abfa8cf5d916cc58f28be70199bde9128a49777c6d8eb743157d14e180bb8ada3d213b762ee553da9dad72b67e1da5a1fc0a807"
        )

        when:
        def act = Flux.just(p1)
                .map { ByteBuffer.wrap(it) }
                .transform(converter.reader())
                .map { ByteBuffer buf ->
                    return Hex.encodeHexString(buf.array())
                }

        then:
        StepVerifier.create(act)
                .expectNext("23ea79402c915aa1624e2f0fcccdfb29857581a971b060ee39137b7bd2e5862f09642df91e163bd10f8bd6ac8ccab7b57b3fa02d23709104dc1db91e509b1a2800680a24080112202a7f5034210041fbeaae933bbb992aac05ae3163c5f32867142729df9405c0bb1240cec8f7055a59f7a2238de5db4abfa8cf5d916cc58f28be70199bde9128a49777c6d8eb743157d14e180bb8ada3d213b762ee553da9dad72b67e1da5a1fc0a807")
                .expectComplete()
                .verify(Duration.ofSeconds(1))
    }

    def "2 byte - read 4 packets"() {
        setup:
        def converter = SizePrefixed.TwoBytes()
        def p1 = Hex.decodeHex(
                "0030" + "bd093a4bdd1e6c55de61d6e26d7b2e5dfe67176f8a2d50d8c92fb3f30424ad910e6ad58c84bb4523ff3a519423cf1844"+
                "003c" + "074adf05ee8fdd14ee1b86849ef7ddf4a3827c39795567c4e6b9e858a6a3fe5c138c79951ca7f61fe65b6edf0b8f63b455e82cecb79ef7f0a9136638"+
                "001c" + "0034a30530296cd2d849a4d98f5621495b29c6ef479fda42b739271f" +
                "0030" + "30d81f777b8fbb060dcba1180eda73675138ed982732b4b9ac53f56aa95fe5fa665493492a86a5e043b1ba2e1c105382"
        )

        when:
        def act = Flux.just(p1)
                .map { ByteBuffer.wrap(it) }
                .transform(converter.reader())
                .map { ByteBuffer buf ->
                    return Hex.encodeHexString(buf.array())
                }

        then:
        StepVerifier.create(act)
                .expectNext("bd093a4bdd1e6c55de61d6e26d7b2e5dfe67176f8a2d50d8c92fb3f30424ad910e6ad58c84bb4523ff3a519423cf1844")
                .expectNext("074adf05ee8fdd14ee1b86849ef7ddf4a3827c39795567c4e6b9e858a6a3fe5c138c79951ca7f61fe65b6edf0b8f63b455e82cecb79ef7f0a9136638")
                .expectNext("0034a30530296cd2d849a4d98f5621495b29c6ef479fda42b739271f")
                .expectNext("30d81f777b8fbb060dcba1180eda73675138ed982732b4b9ac53f56aa95fe5fa665493492a86a5e043b1ba2e1c105382")
                .expectComplete()
                .verify(Duration.ofSeconds(1))
    }

    def "2 byte - read value"() {
        setup:
        def prefix = SizePrefixed.TwoBytes().prefix
        expect:
        prefix.read(ByteBuffer.wrap(Hex.decodeHex(hex))) == value
        where:
        hex         | value
        "0000"  | 0
        "0001"  | 1
        "0024"  | 36
        "0030"  | 48
        "00ca"  | 202
        "00f0"  | 240
        "00ff"  | 255
        "0100"  | 256
        "1234"  | 4660
        "4321"  | 17185
        "f0f0"  | 61680
        "f000"  | 61440
        "f00f"  | 61455
        "ffff"  | 65535
    }


    def "2 byte - write value"() {
        setup:
        def prefix = SizePrefixed.TwoBytes().prefix
        expect:
        Hex.encodeHexString(prefix.write(value)) == hex
        where:
        hex         | value
        "0000"  | 0
        "0001"  | 1
        "0024"  | 36
        "0030"  | 48
        "00ca"  | 202
        "00f0"  | 240
        "00ff"  | 255
        "0100"  | 256
        "1234"  | 4660
        "4321"  | 17185
        "f0f0"  | 61680
        "f000"  | 61440
        "f00f"  | 61455
        "ffff"  | 65535
    }
}
