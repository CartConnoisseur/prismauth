package sh.cxl.prismauth.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import sh.cxl.prismauth.Account;
import sh.cxl.prismauth.PrismAuth;

import java.time.Instant;

public class AccountsScreen extends Screen {
    public AccountsScreen() {
        this(Text.of("accounts"));
    }

    protected AccountsScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        int i = 0;
        for (Account account : PrismAuth.loadAccounts()) {
            ButtonWidget button = ButtonWidget.builder(Text.of(account.profile.name()), b -> {
                        account.login();
                        this.close();
                    })
                    .dimensions(this.width / 2 - 50, 10 + (25 * i), 100, 20)
                    .build();

            if (account.ygg.exp().isBefore(Instant.now())) {
                button.active = false;
                button.setTooltip(Tooltip.of(Text.of("expired, refresh in prism")));
            }

            this.addDrawableChild(button);
            i++;
        }
    }
}
