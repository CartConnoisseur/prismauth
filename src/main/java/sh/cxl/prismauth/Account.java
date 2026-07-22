package sh.cxl.prismauth;

import com.google.gson.annotations.SerializedName;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.ServicesKeyType;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.util.Util;
import sh.cxl.prismauth.mixin.FileCacheAccessor;
import sh.cxl.prismauth.mixin.MinecraftClientAccessor;
import sh.cxl.prismauth.mixin.PlayerSkinProviderAccessor;

import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Account {
    public record Token(String token, @SerializedName("refresh_token") String refreshToken, Instant iat, Instant exp) {}
    public record Profile(String name, UUID id) {}

    public Profile profile;

    Token msa;
    @SerializedName("xrp-mc") Token xrp;
    public Token ygg;

    public boolean active = false;

    //TODO: refreshes or some shit idk
    public void login() {
        MinecraftClient mc = MinecraftClient.getInstance();

        YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(MinecraftClient.getInstance().getNetworkProxy());
        MinecraftClientAccessor mca = (MinecraftClientAccessor) MinecraftClient.getInstance();
        SignatureVerifier.create(authenticationService.getServicesKeySet(), ServicesKeyType.PROFILE_KEY);
        PlayerSkinProvider.FileCache skinCache = ((PlayerSkinProviderAccessor) mc.getSkinProvider()).prismauth$getSkinCache();
        Path skinCachePath = ((FileCacheAccessor) skinCache).prismauth$getDirectory();

        mca.prismauth$setAuthenticationService(authenticationService);
        mca.prismauth$setSessionService(authenticationService.createMinecraftSessionService());
        mca.prismauth$setSkinProvider(new PlayerSkinProvider(mc.getTextureManager(), skinCachePath, mc.getSessionService(), mc));

        Session session = new Session(profile.name, profile.id, ygg.token, null, null, null);
        mca.prismauth$setSession(session);

        UserApiService userApiService = authenticationService.createUserApiService(session.getAccessToken());
        mca.prismauth$setUserApiService(userApiService);
        mca.prismauth$setSocialInteractionsManager(new SocialInteractionsManager(mc, userApiService));
        mca.prismauth$setProfileKeys(ProfileKeys.create(userApiService, session, mc.runDirectory.toPath()));
        mca.prismauth$setAbuseReportContext(AbuseReportContext.create(ReporterEnvironment.ofIntegratedServer(), userApiService));
        mca.prismauth$setGameProfileFuture(CompletableFuture.supplyAsync(() -> mc.getSessionService().fetchProfile(mc.getGameProfile().getId(), true), Util.getIoWorkerExecutor()));
    }
}
