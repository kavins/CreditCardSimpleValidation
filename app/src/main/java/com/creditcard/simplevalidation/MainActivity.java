package com.creditcard.simplevalidation;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.creditcard.simplevalidation.databinding.ActivityMainBinding;
import com.creditcard.simplevalidation.utils.CardType;
import com.creditcard.simplevalidation.view.CardEditText;

/**
 * View that holds the card layout, which also handles different callbacks and update the UI and notify user if required.
 */
public class MainActivity extends AppCompatActivity implements CardEditText.OnCardTypeChangedListener, View.OnTouchListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        init();
    }

    /**
     * Initializer method where all the basic initializations and listeners are set up.
     */
    private void init() {
        binding.edtCardNumber.setOnCardTypeChangedListener(this);
        binding.edtCardNumberHint.setOnTouchListener(this);
    }

    /**
     * When a card type is changed this callback is triggered from card edit text {@link com.creditcard.simplevalidation.view.CardEditText}.
     * Currently we are updating the card icon type here.
     * @param cardType - the updated card type
     */
    @Override
    public void onCardTypeChanged(CardType cardType) {
        binding.imgCardType.setVisibility(View.VISIBLE);
        binding.imgCardType.setImageResource(cardType.getFrontResource());
    }

    /**
     * When a complete card number is entered but it is an invalid card number, this callback is triggered from card edit text {@link com.creditcard.simplevalidation.view.CardEditText}.
     * Currently we are displaying a simple error text to user.
     */
    @Override
    public void onCardValidationError() {
        binding.txtError.setVisibility(View.VISIBLE);
        binding.txtError.setText(getString(R.string.card_info_not_found));
    }

    /**
     * Whenever text change happens callback is triggered from card edit text {@link com.creditcard.simplevalidation.view.CardEditText}.
     * If error view is displayed that is hidden and if the card edit text is completely cleared then we are hiding the card type icon view.
     * Also the hint field is reset to default hint. Else if value present in card input field, then hint is updated accordingly.
     */
    @Override
    public void onTextChange(@Nullable Editable editable, CardType cardType) {
        binding.txtError.setVisibility(View.GONE);

        if (editable == null || editable.length() == 0) {
            binding.imgCardType.setVisibility(View.GONE);
            binding.edtCardNumberHint.setText(getString(R.string.default_hint_text));
        } else {
            binding.edtCardNumberHint.setText(cardType.getCardNumberHint().substring(editable.length()));
            binding.edtCardNumber.addHintSpans(binding.edtCardNumberHint.getEditableText());
        }
    }

    /**
     * On click of hint edit filed, we are redirecting the focus to card number edit field. Also we are pooping up the keyboard if not visible.
     * @param v - View which is touched
     * @param event - the kind of touch motion event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        binding.edtCardNumber.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
        return false;
    }
}
