package com.ideog.android.gallery;


import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class RxHelper {
    public static Observable<Boolean> searchValidatorObservable(EditText search_edit) {

        final PublishSubject<Boolean> subject = PublishSubject.create();

        search_edit.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override public void afterTextChanged(Editable s) {
                subject.onNext(!s.toString().isEmpty());
            }
        });

        return subject;
    }
}
