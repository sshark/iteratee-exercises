package exercise

import java.io.InputStream

import iteratee.IterateeContract._

object Enumerator {
  def apply[E](e: E*): Enumerator[E] = ???
  
  def fromStream(inputStream: InputStream): Enumerator[Byte] = ???
}
