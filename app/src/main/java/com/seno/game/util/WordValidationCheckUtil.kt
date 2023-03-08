package com.seno.game.util

object WordValidationCheckUtil {
    private const val HANGUL_BEGIN_UNICODE = 44032 // 가
        .toChar()
    private const val HANGUL_LAST_UNICODE = 55203 // 힣
        .toChar()
    private const val HANGUL_BASE_UNIT = 588 //각자음 마다 가지는 글자수
        .toChar()

    //자음
    private val INITIAL_SOUND = charArrayOf('ㄱ',
        'ㄲ',
        'ㄴ',
        'ㄷ',
        'ㄸ',
        'ㄹ',
        'ㅁ',
        'ㅂ',
        'ㅃ',
        'ㅅ',
        'ㅆ',
        'ㅇ',
        'ㅈ',
        'ㅉ',
        'ㅊ',
        'ㅋ',
        'ㅌ',
        'ㅍ',
        'ㅎ')

    /**
     * 해당 문자가 INITIAL_SOUND인지 검사.
     * @param searchar
     * @return
     */
    private fun isInitialSound(searchar: Char): Boolean {
        for (c in INITIAL_SOUND) {
            if (c == searchar) {
                return true
            }
        }
        return false
    }

    /**
     * 해당 문자의 자음을 얻는다.
     *
     * @param c 검사할 문자
     * @return
     */
    private fun getInitialSound(c: Char): Char {
        val hanBegin = c - HANGUL_BEGIN_UNICODE
        val index = hanBegin / HANGUL_BASE_UNIT.code
        return INITIAL_SOUND[index]
    }

    /**
     * 해당 문자가 한글인지 검사
     * @param c 문자 하나
     * @return
     */
    private fun isHangul(c: Char): Boolean {
        return c in HANGUL_BEGIN_UNICODE..HANGUL_LAST_UNICODE
    }

    fun checkValidation(input: String, consonant: String): Boolean {
        if (input.length != consonant.length) {
            return false
        }

        var t: Int
        val seof = input.length - consonant.length
        val slen = consonant.length

        for (i in 0..seof) {
            t = 0
            while (t < slen) {
                if (isInitialSound(consonant[t]) && isHangul(input[i + t])) {
                    //만약 현재 char이 초성이고 value가 한글이면
                    if (getInitialSound(input[i + t]) == consonant[t]) //각각의 초성끼리 같은지 비교한다
                        t++ else break
                } else {
                    //char이 초성이 아니라면
                    if (input[i + t] == consonant[t]) //그냥 같은지 비교한다.
                        t++ else break
                }
            }
            if (t == slen) return true //모두 일치한 결과를 찾으면 true를 리턴한다.
        }
        return false //일치하는 것을 찾지 못했으면 false를 리턴한다.
    }
}