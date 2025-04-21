package app.backend.click_and_buy.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageAction {

    private List<Long> idsToMarkAsRead;
    private List<Long> idsToDelete;

}
