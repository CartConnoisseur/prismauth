package sh.cxl.prismauth.mixin;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.texture.PlayerSkinProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
    @Mutable
    @Accessor("session")
    void prismauth$setSession(Session session);

    @Mutable
    @Accessor("skinProvider")
    void prismauth$setSkinProvider(PlayerSkinProvider skinProvider);

//    @Mutable
//    @Accessor("apiServices")
//    void prismauth$setApiServices(ApiServices apiServices);

    @Mutable
    @Accessor("authenticationService")
    void prismauth$setAuthenticationService(YggdrasilAuthenticationService authenticationService);

    @Mutable
    @Accessor("sessionService")
    void prismauth$setSessionService(MinecraftSessionService sessionService);

    @Mutable
    @Accessor("userApiService")
    void prismauth$setUserApiService(UserApiService userApiService);

    @Mutable
    @Accessor("socialInteractionsManager")
    void prismauth$setSocialInteractionsManager(SocialInteractionsManager socialInteractionsManager);

    @Mutable
    @Accessor("profileKeys")
    void prismauth$setProfileKeys(ProfileKeys profileKeys);

    @Mutable
    @Accessor("abuseReportContext")
    void prismauth$setAbuseReportContext(AbuseReportContext abuseReportContext);

    @Mutable
    @Accessor("gameProfileFuture")
    void prismauth$setGameProfileFuture(CompletableFuture<ProfileResult> gameProfileFuture);
}
