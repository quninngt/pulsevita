package com.healthapp.util

import org.junit.Test
import java.util.Calendar
import org.junit.Assert.*

class SolarTermUtilTest {

    @Test
    fun `May 9 returns 立夏`() {
        val cal = Calendar.getInstance().apply { set(2026, Calendar.MAY, 9) }
        assertEquals("立夏", SolarTermUtil.getSolarTerm(cal)!!.name)
    }

    @Test
    fun `May 4 returns 谷雨`() {
        val cal = Calendar.getInstance().apply { set(2026, Calendar.MAY, 4) }
        assertEquals("谷雨", SolarTermUtil.getSolarTerm(cal)!!.name)
    }

    @Test
    fun `May 5 returns 立夏`() {
        val cal = Calendar.getInstance().apply { set(2026, Calendar.MAY, 5) }
        assertEquals("立夏", SolarTermUtil.getSolarTerm(cal)!!.name)
    }

    @Test
    fun `May 21 returns 小满`() {
        val cal = Calendar.getInstance().apply { set(2026, Calendar.MAY, 21) }
        assertEquals("小满", SolarTermUtil.getSolarTerm(cal)!!.name)
    }

    @Test
    fun `Apr 5 returns 清明`() {
        val cal = Calendar.getInstance().apply { set(2026, Calendar.APRIL, 5) }
        assertEquals("清明", SolarTermUtil.getSolarTerm(cal)!!.name)
    }

    @Test
    fun `Apr 20 returns 谷雨`() {
        val cal = Calendar.getInstance().apply { set(2026, Calendar.APRIL, 20) }
        assertEquals("谷雨", SolarTermUtil.getSolarTerm(cal)!!.name)
    }

    @Test
    fun `Feb 4 returns 立春`() {
        val cal = Calendar.getInstance().apply { set(2026, Calendar.FEBRUARY, 4) }
        assertEquals("立春", SolarTermUtil.getSolarTerm(cal)!!.name)
    }

    @Test
    fun `Jan 1 returns 冬至`() {
        val cal = Calendar.getInstance().apply { set(2026, Calendar.JANUARY, 1) }
        assertEquals("冬至", SolarTermUtil.getSolarTerm(cal)!!.name)
    }

    @Test
    fun `Jan 4 returns 冬至`() {
        val cal = Calendar.getInstance().apply { set(2026, Calendar.JANUARY, 4) }
        assertEquals("冬至", SolarTermUtil.getSolarTerm(cal)!!.name)
    }

    @Test
    fun `Jan 5 returns 小寒`() {
        val cal = Calendar.getInstance().apply { set(2026, Calendar.JANUARY, 5) }
        assertEquals("小寒", SolarTermUtil.getSolarTerm(cal)!!.name)
    }

    @Test
    fun `Jan 7 returns 小寒`() {
        val cal = Calendar.getInstance().apply { set(2026, Calendar.JANUARY, 7) }
        assertEquals("小寒", SolarTermUtil.getSolarTerm(cal)!!.name)
    }

    @Test
    fun `Jan 20 returns 大寒`() {
        val cal = Calendar.getInstance().apply { set(2026, Calendar.JANUARY, 20) }
        assertEquals("大寒", SolarTermUtil.getSolarTerm(cal)!!.name)
    }

    @Test
    fun `Dec 22 returns 冬至`() {
        val cal = Calendar.getInstance().apply { set(2026, Calendar.DECEMBER, 22) }
        assertEquals("冬至", SolarTermUtil.getSolarTerm(cal)!!.name)
    }
}
