import iteratee.IterateeContract.Iteratee
import iteratee.LazyContext
import org.specs2.matcher.MatchResult
import org.specs2.mutable._

//uncomment this to run the tests with solution
//import solution._
import solution.Exercise._
import solution._

class ExerciseSpec extends Specification {
  
  def eval[E, A](i: LazyContext[Iteratee[E, A]]): A = {
    val iteratee = i.run
    iteratee.run
  }

  "Warming up" should {
    "count number of elements in list " in {
      val li = Enumerator(1, 2, 3, 4).apply(counter)
      val res: MatchResult[Any] = eval(li) must_== 4
      res
    }
    "sum all the numbers" in {
      val li = Enumerator(1, 2, 3, 4).apply(sum)
      eval(li) must_== 10	
    }
    "find the head element'" in {
      val li = Enumerator("a", "b", "c", "d").apply(head)
      eval(li) must beSome("a")
    }
    "drop first 3 elements'" in {
      val li = Enumerator("a", "b", "c", "d")(drop(2))
      eval(li) must_== List("c", "d")
    }

    "drop all leading 'a' elements'" in {
      val li = Enumerator("a", "a", "a", "b", "c", "a", "d")(drop(_ == "a"))
      eval(li) must_== List("b", "c", "a", "d")
    }
  }


  class MyStream(bytes: Array[Byte]) extends java.io.ByteArrayInputStream(bytes) {
    private[this] var _isClosed = false
    def isClosed = _isClosed
    override def close(): Unit = {
      _isClosed = true
      super.close()
    }
  }

  "Reading from file" should {
    "counter bytes from file" in {

      val stream = new MyStream("1234567".getBytes())
      val li = Enumerator.fromStream(stream).apply(counter)
      eval(li) must_== 7      
      stream.isClosed must beTrue
    }
    
    "getting just the head element" in {
      val stream = new MyStream(Array(1, 2, 3, 4, 5))      
      val li = Enumerator.fromStream(stream).apply(head)
      eval(li) must beSome(1)      
      stream.isClosed must beTrue
      
    }

    "getting just the 4th element" in {
      val stream = new MyStream(Array(1, 2, 3, 4, 5)) 
      val li = Enumerator.fromStream(stream).apply(getElementAt(3))
      eval(li) must beSome(4)  
      stream.isClosed must beTrue      
    }    
  }
  
  
  "Doing transformation" should {
    "ints to strings" in {
      val chars = 'a' to 'z' 
      val li = Enumerator(chars:_*).
      	through(Enumeratee.map(i => i.toString.toUpperCase)).
      	apply(collect)
      eval(li) must_== ('A' to 'Z').toList.map(i => i.toString.toUpperCase)
    }
    
    "read first line" in {
      val li = Enumerator(List('a', 'b', 'c', '\n', '1', '2', '3'):_*).apply(lineIteratee)
      eval(li) must_== "abc"
    }
  }
}