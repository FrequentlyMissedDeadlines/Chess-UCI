package com.frequentlymisseddeadlines.chessuci;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class UciProtocol {
    @Getter @Setter
    private Object something;

    public int dummyMethod(int i) {
        return i + 1;
    }
}
