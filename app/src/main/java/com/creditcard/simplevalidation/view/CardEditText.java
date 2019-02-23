package com.creditcard.simplevalidation.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.creditcard.simplevalidation.utils.CardType;

/**
 * An {@link android.widget.EditText} that has that controls the callbacks to show the Card icons and Error messages based on the number entered.
 */
public class CardEditText extends AppCompatEditText implements TextWatcher {

    /**
     * Callback used to notify View about card type change, validation error and text change if we need to control some UI elements.
     */
    public interface OnCardTypeChangedListener {
        void onCardTypeChanged(CardType cardType);

        void onCardValidationError();

        void onTextChange(@Nullable Editable editable, CardType cardType);
    }

    private CardType mCardType;
    private OnCardTypeChangedListener mOnCardTypeChangedListener;

    public CardEditText(Context context) {
        super(context);
        init();
    }

    public CardEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CardEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setInputType(InputType.TYPE_CLASS_NUMBER);
        addTextChangedListener(this);
        updateCardType(null);
    }

    /**
     * @return The {@link com.creditcard.simplevalidation.utils.CardType} currently entered in
     * the {@link android.widget.EditText}
     */
    public CardType getCardType() {
        return mCardType;
    }

    /**
     * Receive a callback when the {@link com.creditcard.simplevalidation.utils.CardType} changes
     * @param listener to be called when the {@link com.creditcard.simplevalidation.utils.CardType}
     *  changes
     */
    public void setOnCardTypeChangedListener(OnCardTypeChangedListener listener) {
        mOnCardTypeChangedListener = listener;
    }

    /**
     * Method that udpates the card type on every card number change. It also removes and updates the space indices in the view.
     * @param editable - the latest text in the card input field
     */
    @Override
    public void afterTextChanged(Editable editable) {
        Object[] paddingSpans = editable.getSpans(0, editable.length(), SpaceSpan.class);
        for (Object span : paddingSpans) {
            editable.removeSpan(span);
        }

        updateCardType(editable);

        addSpans(editable);
    }

    /**
     * Method used to decide and update the card type {@link com.creditcard.simplevalidation.utils.CardType}.
     * Also it triggers the necessary callbacks to view.
     * @param editable - the latest text in the card input field
     */
    private void updateCardType(Editable editable) {
        CardType type = CardType.getCardType(getText().toString());
        if (mCardType != type) {
            mCardType = type;

            InputFilter[] filters = { new LengthFilter(mCardType.getMinCardLength())};
            setFilters(filters);
            invalidate();

            if (mOnCardTypeChangedListener != null) {
                mOnCardTypeChangedListener.onCardTypeChanged(mCardType);
            }
        }

        if (mOnCardTypeChangedListener != null) {
            mOnCardTypeChangedListener.onTextChange(editable, mCardType);

            if (length() == mCardType.getMinCardLength() && !mCardType.validate(getText().toString())) {
                mOnCardTypeChangedListener.onCardValidationError();
            }
        }
    }

    /**
     * Method used to add the required space span at the space indices for different card types.
     * @param editable - the latest text in the card input field
     */
    private void addSpans(Editable editable) {
        final int length = editable.length();
        for (int index : mCardType.getSpaceIndices()) {
            if (index <= length) {
                editable.setSpan(new SpaceSpan(), index - 1, index,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    /**
     * Method used to add the required space span for the card number hint at the space indices for different card types.
     * @param editable - the latest text in the card input field
     */
    public void addHintSpans(Editable editable) {
        Object[] paddingSpans = editable.getSpans(0, editable.length(), SpaceSpan.class);
        for (Object span : paddingSpans) {
            editable.removeSpan(span);
        }

        final int length = editable.length();
        for (int index : mCardType.getSpaceIndices()) {
            int hintIndex = index - length();
            if (hintIndex > 0 && hintIndex < length) {
                editable.setSpan(new SpaceSpan(), hintIndex - 1, hintIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}
}
