package sh.cxl.prismauth;

import com.google.gson.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sh.cxl.prismauth.screen.AccountsScreen;
import sh.cxl.prismauth.serialization.InstantSerializer;
import sh.cxl.prismauth.serialization.UUIDSerializer;

import java.io.*;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class PrismAuth implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("prismauth");

    private record PrismAccountsFile(List<Account> accounts, int formatVersion) {}

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantSerializer())
            .registerTypeAdapter(UUID.class, new UUIDSerializer())
            .create();

    @Override
    public void onInitializeClient() {
        final AtomicBoolean first = new AtomicBoolean(true);
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen) {
                // ClientLifecycleEvents.CLIENT_STARTED is too early
                if (first.getAndSet(false)) {
                    if (client.getSession().getAccessToken().equals("FabricMC")) {
                        LOGGER.info("fabric token detected, logging in with active account");
                        loadAccounts().stream().filter(account -> account.active).findFirst().ifPresent(Account::login);
                    }
                }

                Screens.getButtons(screen).add(
                        ButtonWidget.builder(Text.of("accounts"), button -> MinecraftClient.getInstance().setScreen(new AccountsScreen()))
                                .dimensions(scaledWidth - 60, 0, 60, 20)
                                .build()
                );
            }
        });
    }

    public static List<Account> loadAccounts() {
        try (Reader reader = new FileReader(getAccountsFile().toFile())) {
            PrismAccountsFile prismAccounts = GSON.fromJson(reader, PrismAccountsFile.class);
            return prismAccounts.accounts;
        } catch (IOException e) {
            LOGGER.error("failed to load prism accounts.json: {}", e.getMessage());
            return List.of();
        }
    }

    private static Path getAccountsFile() {
        return getPrismDir().resolve("accounts.json");
    }

    private static Path getPrismDir() {
        return getEnvPathOrDeafult("XDG_DATA_HOME", Path.of(System.getenv("HOME"))).resolve(".local/share").resolve("PrismLauncher");
    }

    private static Path getEnvPathOrDeafult(String key, Path def) {
        String value = System.getenv(key);
        if (value != null) {
            return Path.of(value);
        } else {
            return def;
        }
    }
}
