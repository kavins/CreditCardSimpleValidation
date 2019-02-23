package com.creditcard.simplevalidation.utils;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;

import com.creditcard.simplevalidation.R;
import com.creditcard.simplevalidation.view.SpaceSpan;

import java.util.regex.Pattern;

/**
 * Card types and related formatting and validation rules.
 * @see <a href="https://github.com/braintree/android-card-form">Some reference and icons are used from here</a>
 */
public enum CardType {

    VISA("^4\\d*",
            R.drawable.ic_visa,
            16, 16),
    MASTERCARD("^(5[1-5]|222[1-9]|22[3-9]|2[3-6]|27[0-1]|2720)\\d*",
            R.drawable.ic_mastercard,
            16, 16),
    DISCOVER("^(6011|65|64[4-9]|622)\\d*",
            R.drawable.ic_discover,
            16, 16),
    AMEX("^3[47]\\d*",
            R.drawable.ic_amex,
            15, 15),
    DINERS_CLUB("^(36|38|30[0-5])\\d*",
            R.drawable.ic_diners_club,
            14, 14),
    INSTA_PAYMENT("^(637|638|639)\\d*",
            R.drawable.ic_maestro, // Note: Need to be replaced with Insta Payment icon
            16, 16),
    JCB("^35\\d*",
            R.drawable.ic_jcb,
            16, 16),
    MAESTRO("^(5018|5020|5038|5[6-9]|6020|6304|6703|6759|676[1-3])\\d*",
            R.drawable.ic_maestro,
            12, 16),
    UNKNOWN("\\d+",
            R.drawable.ic_unknown,
            16, 16);

    private static final int[] AMEX_DINERS_SPACE_INDICES = { 4, 10 };
    private static final int[] DEFAULT_SPACE_INDICES = { 4, 8, 12 };

    private final Pattern mPattern;
    private final int mFrontResource;
    private final int mMinCardLength;
    private final int mMaxCardLength;

    CardType(String regex, int frontResource, int minCardLength, int maxCardLength) {
        mPattern = Pattern.compile(regex);
        mFrontResource = frontResource;
        mMinCardLength = minCardLength;
        mMaxCardLength = maxCardLength;
    }

    /**
     * Returns the card type matching this account, or {@link com.creditcard.simplevalidation.utils.CardType#UNKNOWN}
     * for no match.
     * <p/>
     * A partial account type may be given, with the caveat that it may not have enough digits to
     * match.
     */
    public static CardType getCardType(String cardNumber) {
        for (CardType cardType : values()) {
            if (cardType.getPattern().matcher(cardNumber).matches()) {
                return cardType;
            }
        }

        return UNKNOWN;
    }

    /**
     * @return The regex matching this card type.
     */
    public Pattern getPattern() {
        return mPattern;
    }

    /**
     * @return The android resource id for the front card image, highlighting card number format.
     */
    public int getFrontResource() {
        return mFrontResource;
    }

    /**
     * @return minimum length of a card for this {@link com.creditcard.simplevalidation.utils.CardType}
     */
    public int getMinCardLength() {
        return mMinCardLength;
    }

    /**
     * @return max length of a card for this {@link com.creditcard.simplevalidation.utils.CardType}
     */
    public int getMaxCardLength() {
        return mMaxCardLength;
    }

    /**
     * @return the locations where spaces should be inserted when formatting the card in a user
     * friendly way. Only for display purposes.
     */
    public int[] getSpaceIndices() {
        return this == AMEX || this == DINERS_CLUB ? AMEX_DINERS_SPACE_INDICES : DEFAULT_SPACE_INDICES;
    }

    /**
     * @return the card number hint to display with respective space indices.
     */
    public String getCardNumberHint() {
        StringBuilder hintBuilder = new StringBuilder();
        for (int i = 0; i < getMinCardLength(); i++) {
            hintBuilder.append("X");
        }

        return hintBuilder.toString();
    }

    /**
     * Adds the space at the required indices for a card type hint
     * @param hintBuilder - the card type hint in which space should be added
     */
    public void addSpans(SpannableStringBuilder hintBuilder) {
        final int length = hintBuilder.length();
        for (int index : getSpaceIndices()) {
            if (index <= length) {
                hintBuilder.setSpan(new SpaceSpan(), index - 1, index,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    /**
     * Performs the Luhn check on the given card number.
     *
     * @param cardNumber a String consisting of numeric digits (only).
     * @return {@code true} if the sequence passes the checksum
     * @throws IllegalArgumentException if {@code cardNumber} contained a non-digit (where {@link
     * Character#isDefined(char)} is {@code false}).
     * @see <a href="https://www.freeformatter.com/credit-card-number-generator-validator.html">Luhn Algorithm Logic Requirement</a>
     */
    public static boolean isLuhnValid(String cardNumber) {
        final String reversed = new StringBuffer(cardNumber).reverse().toString();
        final int len = reversed.length();
        int oddSum = 0;
        int evenSum = 0;
        for (int i = 1; i < len; i++) {
            final int digit = getNumericValueFromChar(reversed.charAt(i));
            if (i % 2 == 0) {
                oddSum += digit;
            } else {
                evenSum += digit / 5 + (2 * digit) % 10;
            }
        }
        return (oddSum + evenSum) % 10 == getNumericValueFromChar(reversed.charAt(0));
    }

    /**
     * Check whether the passed character is a digit and if so return the numeric value of it
     * @throws IllegalArgumentException if the passed character is not a digit
     * @param c - a character digit
     * @return the numeric value of the character digit
     */
    private static int getNumericValueFromChar(char c) {
        if (!Character.isDigit(c)) {
            throw new IllegalArgumentException(String.format("Not a digit: '%s'", c));
        }
        return Character.getNumericValue(c);
    }

    /**
     * @param cardNumber The card number to validate.
     * @return {@code true} if this card number is locally valid.
     */
    public boolean validate(String cardNumber) {
        if (TextUtils.isEmpty(cardNumber)) {
            return false;
        } else if (!TextUtils.isDigitsOnly(cardNumber)) {
            return false;
        }

        final int numberLength = cardNumber.length();
        if (numberLength < mMinCardLength || numberLength > mMaxCardLength) {
            return false;
        } else if (!mPattern.matcher(cardNumber).matches()) {
            return false;
        }

        return isLuhnValid(cardNumber);
    }
}