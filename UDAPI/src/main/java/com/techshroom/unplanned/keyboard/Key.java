package com.techshroom.unplanned.keyboard;

import org.lwjgl.glfw.GLFW;

/**
 * Key enum values for the {@link GLFW} class.
 *
 * @author Kenzie Togami
 */
public enum Key {
    /**
     * The value corresponding to an unknown key.
     */
    UNKNOWN(GLFW.GLFW_KEY_UNKNOWN),

    /**
     * The value corresponding to the key 'SPACE'.
     */
    SPACE(GLFW.GLFW_KEY_SPACE),

    /**
     * The value corresponding to the key 'APOSTROPHE'.
     */
    APOSTROPHE(GLFW.GLFW_KEY_APOSTROPHE),

    /**
     * The value corresponding to the key 'COMMA'.
     */
    COMMA(GLFW.GLFW_KEY_COMMA),

    /**
     * The value corresponding to the key 'MINUS'.
     */
    MINUS(GLFW.GLFW_KEY_MINUS),

    /**
     * The value corresponding to the key 'PERIOD'.
     */
    PERIOD(GLFW.GLFW_KEY_PERIOD),

    /**
     * The value corresponding to the key 'SLASH'.
     */
    SLASH(GLFW.GLFW_KEY_SLASH),

    /**
     * The value corresponding to the key '0'.
     */
    NUM_0(GLFW.GLFW_KEY_0),

    /**
     * The value corresponding to the key '1'.
     */
    NUM_1(GLFW.GLFW_KEY_1),

    /**
     * The value corresponding to the key '2'.
     */
    NUM_2(GLFW.GLFW_KEY_2),

    /**
     * The value corresponding to the key '3'.
     */
    NUM_3(GLFW.GLFW_KEY_3),

    /**
     * The value corresponding to the key '4'.
     */
    NUM_4(GLFW.GLFW_KEY_4),

    /**
     * The value corresponding to the key '5'.
     */
    NUM_5(GLFW.GLFW_KEY_5),

    /**
     * The value corresponding to the key '6'.
     */
    NUM_6(GLFW.GLFW_KEY_6),

    /**
     * The value corresponding to the key '7'.
     */
    NUM_7(GLFW.GLFW_KEY_7),

    /**
     * The value corresponding to the key '8'.
     */
    NUM_8(GLFW.GLFW_KEY_8),

    /**
     * The value corresponding to the key '9'.
     */
    NUM_9(GLFW.GLFW_KEY_9),

    /**
     * The value corresponding to the key 'SEMICOLON'.
     */
    SEMICOLON(GLFW.GLFW_KEY_SEMICOLON),

    /**
     * The value corresponding to the key 'EQUAL'.
     */
    EQUAL(GLFW.GLFW_KEY_EQUAL),

    /**
     * The value corresponding to the key 'A'.
     */
    A(GLFW.GLFW_KEY_A),

    /**
     * The value corresponding to the key 'B'.
     */
    B(GLFW.GLFW_KEY_B),

    /**
     * The value corresponding to the key 'C'.
     */
    C(GLFW.GLFW_KEY_C),

    /**
     * The value corresponding to the key 'D'.
     */
    D(GLFW.GLFW_KEY_D),

    /**
     * The value corresponding to the key 'E'.
     */
    E(GLFW.GLFW_KEY_E),

    /**
     * The value corresponding to the key 'F'.
     */
    F(GLFW.GLFW_KEY_F),

    /**
     * The value corresponding to the key 'G'.
     */
    G(GLFW.GLFW_KEY_G),

    /**
     * The value corresponding to the key 'H'.
     */
    H(GLFW.GLFW_KEY_H),

    /**
     * The value corresponding to the key 'I'.
     */
    I(GLFW.GLFW_KEY_I),

    /**
     * The value corresponding to the key 'J'.
     */
    J(GLFW.GLFW_KEY_J),

    /**
     * The value corresponding to the key 'K'.
     */
    K(GLFW.GLFW_KEY_K),

    /**
     * The value corresponding to the key 'L'.
     */
    L(GLFW.GLFW_KEY_L),

    /**
     * The value corresponding to the key 'M'.
     */
    M(GLFW.GLFW_KEY_M),

    /**
     * The value corresponding to the key 'N'.
     */
    N(GLFW.GLFW_KEY_N),

    /**
     * The value corresponding to the key 'O'.
     */
    O(GLFW.GLFW_KEY_O),

    /**
     * The value corresponding to the key 'P'.
     */
    P(GLFW.GLFW_KEY_P),

    /**
     * The value corresponding to the key 'Q'.
     */
    Q(GLFW.GLFW_KEY_Q),

    /**
     * The value corresponding to the key 'R'.
     */
    R(GLFW.GLFW_KEY_R),

    /**
     * The value corresponding to the key 'S'.
     */
    S(GLFW.GLFW_KEY_S),

    /**
     * The value corresponding to the key 'T'.
     */
    T(GLFW.GLFW_KEY_T),

    /**
     * The value corresponding to the key 'U'.
     */
    U(GLFW.GLFW_KEY_U),

    /**
     * The value corresponding to the key 'V'.
     */
    V(GLFW.GLFW_KEY_V),

    /**
     * The value corresponding to the key 'W'.
     */
    W(GLFW.GLFW_KEY_W),

    /**
     * The value corresponding to the key 'X'.
     */
    X(GLFW.GLFW_KEY_X),

    /**
     * The value corresponding to the key 'Y'.
     */
    Y(GLFW.GLFW_KEY_Y),

    /**
     * The value corresponding to the key 'Z'.
     */
    Z(GLFW.GLFW_KEY_Z),

    /**
     * The value corresponding to the key 'Left BRACKET'.
     */
    LEFT_BRACKET(GLFW.GLFW_KEY_LEFT_BRACKET),

    /**
     * The value corresponding to the key 'BACKSLASH'.
     */
    BACKSLASH(GLFW.GLFW_KEY_BACKSLASH),

    /**
     * The value corresponding to the key 'Right BRACKET'.
     */
    RIGHT_BRACKET(GLFW.GLFW_KEY_RIGHT_BRACKET),

    /**
     * The value corresponding to the key 'GRAVE_ACCENT'.
     */
    GRAVE_ACCENT(GLFW.GLFW_KEY_GRAVE_ACCENT),

    /**
     * The value corresponding to the key 'WORLD_1'.
     */
    WORLD_1(GLFW.GLFW_KEY_WORLD_1),

    /**
     * The value corresponding to the key 'WORLD_2'.
     */
    WORLD_2(GLFW.GLFW_KEY_WORLD_2),

    /**
     * The value corresponding to the key 'ESCAPE'.
     */
    ESCAPE(GLFW.GLFW_KEY_ESCAPE),

    /**
     * The value corresponding to the key 'ENTER'.
     */
    ENTER(GLFW.GLFW_KEY_ENTER),

    /**
     * The value corresponding to the key 'TAB'.
     */
    TAB(GLFW.GLFW_KEY_TAB),

    /**
     * The value corresponding to the key 'BACKSPACE'.
     */
    BACKSPACE(GLFW.GLFW_KEY_BACKSPACE),

    /**
     * The value corresponding to the key 'INSERT'.
     */
    INSERT(GLFW.GLFW_KEY_INSERT),

    /**
     * The value corresponding to the key 'DELETE'.
     */
    DELETE(GLFW.GLFW_KEY_DELETE),

    /**
     * The value corresponding to the key 'RIGHT'.
     */
    RIGHT(GLFW.GLFW_KEY_RIGHT),

    /**
     * The value corresponding to the key 'LEFT'.
     */
    LEFT(GLFW.GLFW_KEY_LEFT),

    /**
     * The value corresponding to the key 'DOWN'.
     */
    DOWN(GLFW.GLFW_KEY_DOWN),

    /**
     * The value corresponding to the key 'UP'.
     */
    UP(GLFW.GLFW_KEY_UP),

    /**
     * The value corresponding to the key 'PAGE_UP'.
     */
    PAGE_UP(GLFW.GLFW_KEY_PAGE_UP),

    /**
     * The value corresponding to the key 'PAGE_DOWN'.
     */
    PAGE_DOWN(GLFW.GLFW_KEY_PAGE_DOWN),

    /**
     * The value corresponding to the key 'HOME'.
     */
    HOME(GLFW.GLFW_KEY_HOME),

    /**
     * The value corresponding to the key 'END'.
     */
    END(GLFW.GLFW_KEY_END),

    /**
     * The value corresponding to the key 'CAPS_LOCK'.
     */
    CAPS_LOCK(GLFW.GLFW_KEY_CAPS_LOCK),

    /**
     * The value corresponding to the key 'SCROLL_LOCK'.
     */
    SCROLL_LOCK(GLFW.GLFW_KEY_SCROLL_LOCK),

    /**
     * The value corresponding to the key 'LOCK'.
     */
    NUM_LOCK(GLFW.GLFW_KEY_NUM_LOCK),

    /**
     * The value corresponding to the key 'PRINT_SCREEN'.
     */
    PRINT_SCREEN(GLFW.GLFW_KEY_PRINT_SCREEN),

    /**
     * The value corresponding to the key 'PAUSE'.
     */
    PAUSE(GLFW.GLFW_KEY_PAUSE),

    /**
     * The value corresponding to the key 'F1'.
     */
    F1(GLFW.GLFW_KEY_F1),

    /**
     * The value corresponding to the key 'F2'.
     */
    F2(GLFW.GLFW_KEY_F2),

    /**
     * The value corresponding to the key 'F3'.
     */
    F3(GLFW.GLFW_KEY_F3),

    /**
     * The value corresponding to the key 'F4'.
     */
    F4(GLFW.GLFW_KEY_F4),

    /**
     * The value corresponding to the key 'F5'.
     */
    F5(GLFW.GLFW_KEY_F5),

    /**
     * The value corresponding to the key 'F6'.
     */
    F6(GLFW.GLFW_KEY_F6),

    /**
     * The value corresponding to the key 'F7'.
     */
    F7(GLFW.GLFW_KEY_F7),

    /**
     * The value corresponding to the key 'F8'.
     */
    F8(GLFW.GLFW_KEY_F8),

    /**
     * The value corresponding to the key 'F9'.
     */
    F9(GLFW.GLFW_KEY_F9),

    /**
     * The value corresponding to the key 'F10'.
     */
    F10(GLFW.GLFW_KEY_F10),

    /**
     * The value corresponding to the key 'F11'.
     */
    F11(GLFW.GLFW_KEY_F11),

    /**
     * The value corresponding to the key 'F12'.
     */
    F12(GLFW.GLFW_KEY_F12),

    /**
     * The value corresponding to the key 'F13'.
     */
    F13(GLFW.GLFW_KEY_F13),

    /**
     * The value corresponding to the key 'F14'.
     */
    F14(GLFW.GLFW_KEY_F14),

    /**
     * The value corresponding to the key 'F15'.
     */
    F15(GLFW.GLFW_KEY_F15),

    /**
     * The value corresponding to the key 'F16'.
     */
    F16(GLFW.GLFW_KEY_F16),

    /**
     * The value corresponding to the key 'F17'.
     */
    F17(GLFW.GLFW_KEY_F17),

    /**
     * The value corresponding to the key 'F18'.
     */
    F18(GLFW.GLFW_KEY_F18),

    /**
     * The value corresponding to the key 'F19'.
     */
    F19(GLFW.GLFW_KEY_F19),

    /**
     * The value corresponding to the key 'F20'.
     */
    F20(GLFW.GLFW_KEY_F20),

    /**
     * The value corresponding to the key 'F21'.
     */
    F21(GLFW.GLFW_KEY_F21),

    /**
     * The value corresponding to the key 'F22'.
     */
    F22(GLFW.GLFW_KEY_F22),

    /**
     * The value corresponding to the key 'F23'.
     */
    F23(GLFW.GLFW_KEY_F23),

    /**
     * The value corresponding to the key 'F24'.
     */
    F24(GLFW.GLFW_KEY_F24),

    /**
     * The value corresponding to the key 'F25'.
     */
    F25(GLFW.GLFW_KEY_F25),

    /**
     * The value corresponding to the key 'Keypad 0'.
     */
    KP_0(GLFW.GLFW_KEY_KP_0),

    /**
     * The value corresponding to the key 'Keypad 1'.
     */
    KP_1(GLFW.GLFW_KEY_KP_1),

    /**
     * The value corresponding to the key 'Keypad 2'.
     */
    KP_2(GLFW.GLFW_KEY_KP_2),

    /**
     * The value corresponding to the key 'Keypad 3'.
     */
    KP_3(GLFW.GLFW_KEY_KP_3),

    /**
     * The value corresponding to the key 'Keypad 4'.
     */
    KP_4(GLFW.GLFW_KEY_KP_4),

    /**
     * The value corresponding to the key 'Keypad 5'.
     */
    KP_5(GLFW.GLFW_KEY_KP_5),

    /**
     * The value corresponding to the key 'Keypad 6'.
     */
    KP_6(GLFW.GLFW_KEY_KP_6),

    /**
     * The value corresponding to the key 'Keypad 7'.
     */
    KP_7(GLFW.GLFW_KEY_KP_7),

    /**
     * The value corresponding to the key 'Keypad 8'.
     */
    KP_8(GLFW.GLFW_KEY_KP_8),

    /**
     * The value corresponding to the key 'Keypad 9'.
     */
    KP_9(GLFW.GLFW_KEY_KP_9),

    /**
     * The value corresponding to the key 'Keypad DECIMAL'.
     */
    KP_DECIMAL(GLFW.GLFW_KEY_KP_DECIMAL),

    /**
     * The value corresponding to the key 'Keypad DIVIDE'.
     */
    KP_DIVIDE(GLFW.GLFW_KEY_KP_DIVIDE),

    /**
     * The value corresponding to the key 'Keypad MULTIPLY'.
     */
    KP_MULTIPLY(GLFW.GLFW_KEY_KP_MULTIPLY),

    /**
     * The value corresponding to the key 'Keypad SUBTRACT'.
     */
    KP_SUBTRACT(GLFW.GLFW_KEY_KP_SUBTRACT),

    /**
     * The value corresponding to the key 'Keypad ADD'.
     */
    KP_ADD(GLFW.GLFW_KEY_KP_ADD),

    /**
     * The value corresponding to the key 'Keypad ENTER'.
     */
    KP_ENTER(GLFW.GLFW_KEY_KP_ENTER),

    /**
     * The value corresponding to the key 'Keypad EQUAL'.
     */
    KP_EQUAL(GLFW.GLFW_KEY_KP_EQUAL),

    /**
     * The value corresponding to the key 'Left SHIFT'.
     */
    LEFT_SHIFT(GLFW.GLFW_KEY_LEFT_SHIFT),

    /**
     * The value corresponding to the key 'Left CONTROL'.
     */
    LEFT_CONTROL(GLFW.GLFW_KEY_LEFT_CONTROL),

    /**
     * The value corresponding to the key 'Left ALT'.
     */
    LEFT_ALT(GLFW.GLFW_KEY_LEFT_ALT),

    /**
     * The value corresponding to the key 'Left SUPER'.
     */
    LEFT_SUPER(GLFW.GLFW_KEY_LEFT_SUPER),

    /**
     * The value corresponding to the key 'Right SHIFT'.
     */
    RIGHT_SHIFT(GLFW.GLFW_KEY_RIGHT_SHIFT),

    /**
     * The value corresponding to the key 'Right CONTROL'.
     */
    RIGHT_CONTROL(GLFW.GLFW_KEY_RIGHT_CONTROL),

    /**
     * The value corresponding to the key 'Right ALT'.
     */
    RIGHT_ALT(GLFW.GLFW_KEY_RIGHT_ALT),

    /**
     * The value corresponding to the key 'Right SUPER'.
     */
    RIGHT_SUPER(GLFW.GLFW_KEY_RIGHT_SUPER),

    /**
     * The value corresponding to the key 'MENU'.
     */
    MENU(GLFW.GLFW_KEY_MENU),

    /**
     * The value corresponding to the key 'LAST'.
     */
    LAST(GLFW.GLFW_KEY_LAST);

    private final int glfwCode;

    private Key(int glfwCode) {
        this.glfwCode = glfwCode;
    }

    /**
     * Gets the corresponding {@code GLFW int code} for the key.
     *
     * @return The corresponding {@code GLFW int code} for the key
     */
    public int getGLFWCode() {
        return this.glfwCode;
    }

    @Override
    public String toString() {
        return "GLFW_KEY_" + name().replaceFirst("NUM_(\\d)", "\\1");
    }
}
