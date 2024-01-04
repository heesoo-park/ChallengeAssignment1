package com.example.challengeassginment1.signup

enum class SignUpEntryType {
    CREATE, // 생성
    UPDATE, // 수정
    DELETE, // 삭제
    READ,   // 읽기
    ;

    companion object {
        // ordinal로 받아온 값(Int형)을 enum class 값으로 변환하여 반환해주는 함수
        // enum class 값 중에서 가장 먼저 ordinal 값이 같은 값을 반환하고 없으면 디폴트로 CREATE를 반환
        fun getEntryType(
            ordinal: Int?
        ): SignUpEntryType {
            return SignUpEntryType.values().firstOrNull() {
                it.ordinal == ordinal
            } ?: CREATE
        }
    }
}