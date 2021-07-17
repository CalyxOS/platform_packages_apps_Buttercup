package org.calyxos.buttercup.model;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import org.calyxos.buttercup.R;
import org.calyxos.buttercup.network.Network;
import org.calyxos.buttercup.network.RequestListener;
import org.calyxos.buttercup.repo.Repository;

public class FeedbackViewModel extends ViewModel {

    private Repository repo;

    public FeedbackViewModel() {
        repo = new Repository();
    }

    public void submitFeedback(Context context, String subject, String body, RequestListener requestListener) {
        if (Network.isConnected(context)) {
            if (!subject.isEmpty() && !body.isEmpty()) {
                repo.submitFeedback(subject, body, requestListener);
            } else requestListener.onValidationFailed(context.getString(R.string.subject_body_empty));
        } else requestListener.onInternetError();
    }

}
