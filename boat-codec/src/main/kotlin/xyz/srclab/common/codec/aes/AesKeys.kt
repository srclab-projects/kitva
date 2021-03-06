@file:JvmName("AesKeys")
@file:JvmMultifileClass

package xyz.srclab.common.codec.aes

import xyz.srclab.common.base.toBytes
import xyz.srclab.common.codec.CodecAlgorithm
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

private const val AES_KEY_INIT_LENGTH = 128

@JvmName("newKey")
fun CharSequence.toAesKey(): SecretKey {
    return this.toBytes().toAesKey()
}

@JvmName("newKey")
fun CharSequence.toAesKey(keySize: Int): SecretKey {
    return this.toBytes().toAesKey(keySize)
}

@JvmOverloads
@JvmName("newKey")
fun ByteArray.toAesKey(keySize: Int = AES_KEY_INIT_LENGTH): SecretKey {
    val result: SecretKeySpec
    val random = SecureRandom.getInstance(CodecAlgorithm.SHA1PRNG_NAME)
    random.setSeed(this)
    val kgen = KeyGenerator.getInstance(CodecAlgorithm.AES_NAME)
    kgen.init(keySize, random)
    val secretKey = kgen.generateKey()
    val enCodeFormat = secretKey.encoded
    result = SecretKeySpec(enCodeFormat, CodecAlgorithm.AES_NAME)
    return result
}