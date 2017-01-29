package net;

import net.pojo.CSGOAchievements;
import net.pojo.CSGOEconSchema;
import net.pojo.CSGOGlobalAchievements;
import net.pojo.CSGOOwnedGames;
import net.pojo.CSGOStatSchema;
import net.pojo.CSGOStats;
import net.pojo.CSGOProfile;
import net.pojo.CSGOSteamID;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface SteamApiService {
    // GET Request for getting the profile of a user
    @GET("ISteamUser/GetPlayerSummaries/v0002/")
    Observable<CSGOProfile> getProfile(@Query("key") String apiKey,
                                       @Query("steamids") String steamUserUniqueId);

    // GET Request for getting the stats/achievements of a user
    @GET("ISteamUserStats/GetUserStatsForGame/v0002/")
    Observable<CSGOStats> getStats(@Query("appid") int gameType,
                                   @Query("key") String apiKey,
                                   @Query("steamid") String steamUserUniqueId);

    // Get Request for getting the favorite weapon name
    @GET("IEconItems_" + AppConstants.CS_GO_APP_ID + "/GetSchema/v0002/")
    Observable<CSGOEconSchema> getWeaponIdInfo(@Query("key") String apiKey);

    // Get Request for getting all available CSGO information
    @GET("ISteamUserStats/GetSchemaForGame/v2/")
    Observable<CSGOStatSchema> getAllAvailableCSGOInfo(@Query("key") String apiKey,
                                                       @Query("appid") int gameType);

    // Get Request for getting all user achievements
    @GET("ISteamUserStats/GetPlayerAchievements/v0001/")
    Observable<CSGOAchievements> getAllAchievements(@Query("appid") int gameType,
                                                    @Query("key") String apiKey,
                                                    @Query("steamid") String steamUserUniqueId);

    // Get Request for getting global CSGO achievement percentages
    @GET("ISteamUserStats/GetGlobalAchievementPercentagesForApp/v0002/")
    Observable<CSGOGlobalAchievements> getGlobalAchievementPercentages(@Query("gameid") int gameType);

    // Get Request to see all owned games (looking to see if they even own CSGO)
    @GET("IPlayerService/GetOwnedGames/v0001/")
    Observable<CSGOOwnedGames> getOwnedGames(@Query("key") String apiKey,
                                             @Query("steamid") String steamUserUniqueId);

    // Get Request to convert the users custom url to the steam 64 id
    @GET("ISteamUser/ResolveVanityURL/v0001/")
    Observable<CSGOSteamID> getSteamId(@Query("key") String apiKey,
                                       @Query("vanityurl") String userVanityUrlName);
}
