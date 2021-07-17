package org.calyxos.buttercup.network;

public interface RequestListener {

    void onInternetError();

    void onValidationFailed(String validationErrorMessage);

    void onConnectionError(String errorMessage);

    void onSuccess();

    void onFail(String failMessage);
}
