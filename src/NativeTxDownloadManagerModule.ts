import type { TurboModule } from "react-native"
import { TurboModuleRegistry } from "react-native"
import type {
  Float,
  Int32,
} from 'react-native/Libraries/Types/CodegenTypes'


type VideoInfo = Readonly<{
  appId: string;
  fileId: string;
  sign: string;
}>

type DownloadInfo = Readonly<{
  dataSource: VideoInfo;
  userName: string;
  duration: Float;
  playableDuration: Float;
  size: Int32;
  downloadSize: Int32;
  segments: Int32;
  downloadSegments: Int32;
  progress: Float;
  playPath: string;
  speed: Int32;
  downloadState: Int32;
  preferredResolution: Int32;
  isResourceBroken: boolean;
}>

export interface Spec extends TurboModule {
  startDownload : (videoInfo: VideoInfo) => void
  stopDownload: (fileId: string, appId: string) => void
  deleteDownload : (fileId: string, appId: string) => void
  getDownloadList : () => DownloadInfo[]
  addListener(eventName: string): void;
  removeListeners(count: Int32): void;
}

export default TurboModuleRegistry.get<Spec>("TxDownloadManager");

