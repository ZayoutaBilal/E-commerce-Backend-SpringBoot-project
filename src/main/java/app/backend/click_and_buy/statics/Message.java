package app.backend.click_and_buy.statics;

import app.backend.click_and_buy.massages.Error;
import app.backend.click_and_buy.massages.Success;
import app.backend.click_and_buy.massages.Warning;
import app.backend.click_and_buy.massages.Subject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {

    private Error messageResponseError = new Error();
    private Success messageResponseSuccess = new Success();
    private Warning messageResponseWarning = new Warning();
    private Subject subject = new Subject();
}
