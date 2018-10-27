package main.scala.com.pgone.util

import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import util.control.Breaks._


/**
  * @author PGOne
  * @ date 2018/10/27
  *
  * Some common string and number conversion tools and some hardcoded tests
  */
object CommUtils {


  /**
    *
    * The returned must be a non-negative number (special handling if the hashCode is negative)
    *
    * Calculates 'x' modulo 'mod', takes to consideration sign of x,
    * i.e. if 'x' is negative, than 'x' % 'mod' is negative too
    * so function return (x % mod) + mod in that case.
    * @param x
    * @param mod
    * @return
    */
  def nonNegativeMod(x: Int, mod: Int): Int = {
    val rawMod = x % mod
    rawMod + (if (rawMod < 0) mod else 0)
  }

  /**
    *
    * return double
    *
    * @param str
    * @param default
    * @return
    */
  def convertDouble(str: String, default: Double = 0.0) = {
    try {
      if(str.trim.equals("")) default else str.trim.toDouble
    } catch {
      case _: Exception => 0.0
    }
  }


  /**
    *
    * Check if the string still has a list of specified substrings, one with one to return true
    *
    * @param str
    * @param subStrArr
    * @return
    */
  def hasSubStr(str: String, subStrArr: Array[String]) = {
    var contains = false
    breakable {
      for(e <- subStrArr) {
        if(str.contains(e)) {
          contains = true
          break()
        }
      }
    }
    contains
  }


  /**
    *
    * Check if the string still has a list of specified substrings, return a list of substring arrays
    *
    * @param str
    * @param subStrArr
    * @return
    */
  def getSubStr(str: String, subStrArr: Array[String]) = {
    val subArray = new ArrayBuffer[String]()
    for(e <- subStrArr) {
      if(str.contains(e)) {
        subArray.append(e)
      }
    }
    subArray
  }




  /**
    *
    * return int
    *
    * @param str
    * @param default
    * @return
    */
  def convertInt(str: String, default: Int = 0) = {
    try {
      if(str.trim.equals("")) default else str.trim.toInt
    } catch {
      case _: Exception => 0
    }
  }

  /**
    *
    * return Long
    *
    * @param str
    * @param default
    * @return
    */
  def convertLong(str: String, default: Long = 0) = {
    try {
      if(str.trim.equals("")) default else str.trim.toLong
    } catch {
      case _: Exception => 0L
    }
  }

  def main(args: Array[String]): Unit = {
    //    contains("INFO WARN", Array("INFO", "WARN"))
  }


}

