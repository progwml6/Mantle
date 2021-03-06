package mantle.internal

import mantle.lib.CoreRepo._

import scala.collection.mutable.MutableList
import cpw.mods.fml.common.{FMLCommonHandler, Loader}
import mantle.internal.crash.{CallableUnsuppConfig, CallableSuppConfig}

/**
 * Environment Checks
 *
 * Checks the runtime environment is safe for use. If not, registers warnings and adds a suitable crash callable.
 *
 * @author Sunstrike <sunstrike@azurenode.net>
 */
object EnvironmentChecks {

  def examineEnvironment() {

    logger.info("Beginning environmental inspection...")

    var modIds = new MutableList[String]()

    if (Loader.isModLoaded("gregtech_addon")) {
      logger.warning("[Environment] Found unsupported mod: gregtech_addon.")
      modIds += "gregtech_addon"
    }

    // Removed 'Classic' Optifine check to work around MCP issue with scalac
    if (/*FMLCommonHandler.instance.getSide == Side.CLIENT && FMLClientHandler.instance.hasOptifine ||*/ Loader.isModLoaded("optifine")) {
      logger.warning("[Environment] Found unsupported mod: optifine")
      modIds += "optifine"
    }

    try {
      val cl = Class.forName("org.bukkit.Bukkit")
      if (cl != null) {
        logger.warning("[Environment] Found unsupported server: bukkit (or derivative thereof)")
        modIds += "bukkit"
      }
    } catch {
      case _:Exception => logger.fine("[Environment] Bukkit check failed. This is perfectly normal (good in fact)")
    }

    if (modIds.size <= 0) {
      logger.info("Environmental inspection clean. Continuing.")
      FMLCommonHandler.instance().registerCrashCallable(new CallableSuppConfig)
    } else {
      logger.warning("Non-fatal issues in environment. Continuing but configuration unsupported.")
      FMLCommonHandler.instance().registerCrashCallable(new CallableUnsuppConfig(modIds.asInstanceOf[List[String]]))
      logger.warning("CallableUnsuppConfig registered. This will mark crash logs as invalid.")
    }

  }

}
