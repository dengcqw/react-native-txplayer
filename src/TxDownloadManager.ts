import { NativeModules, NativeEventEmitter } from 'react-native';
import TurboManager from './NativeTxDownloadManagerModule';

if (TurboManager == null) {
  function isLoaded() {
    // @ts-ignore
    return global.TXD_getDownloadList != null;
  }

  if (!isLoaded()) {
    const result = NativeModules.TxDownloadManager?.install();
    if (!result && !isLoaded()) {
      throw new Error('JSI bindings were not installed for: TxDownloadManager Module');
    }

    if (!isLoaded()) {
      throw new Error('JSI bindings were not installed for: TxDownloadManager Module');
    }
  }
}

export interface VideoInfo {
  appId: string;
  fileId: string;
  sign: string;
}

export interface DownloadInfo {
  // fileid下载对象（url下载时为可选，fileid下载时此参数为必填参数）
  dataSource: VideoInfo;
  // 账户名称, 默认值为default
  userName: string;
  // 时长，单位：秒
  duration: number;
  //可播放时长，单位：秒
  playableDuration: number;
  //文件总大小，单位：byte
  size: number;
  // 已下载大小，单位：byte
  downloadSize: number;
  // 分段总数
  segments: number;
  // 已下载的分段数
  downloadSegments: number;
  // 进度
  progress: number;
  // 播放路径，视频下载完成后可传给TXVodPlayer进行本地文件播放
  // @discussion 此参数用于下载播放时，需要通过getDownloadMediaInfoList或 getDownloadMediaInfo: 接口获取得到，不可以私下保存
  playPath: string;
  // 下载速度，byte每秒
  speed: number;
  // 下载状态
  // 初始化状态 0 启动 1, 停止 2, 错误 3, 下载完成 4,
  downloadState: number;
  // 偏好清晰度，查询播放状态时，需要与下载时保持一致。 默认720P
  preferredResolution: number;
  // 判断资源是否损坏，如下载完被删除等情况，默认值为NO
  isResourceBroken: boolean;
}

export const startDownload = (videoInfo: VideoInfo) => {
  console.log('---->videoInfo', videoInfo)
  try {
    if (TurboManager) {
      TurboManager.startDownload(videoInfo)
    } else {
      // @ts-ignore
      global.TXD_startDownload(JSON.stringify(videoInfo));
    }
  } catch (e) {
    console.log('----> TxDownloadManager startDownload err', e);
  }
};
export const stopDownload = (fileId: string, appId: string) => {
  if (TurboManager) {
    TurboManager.stopDownload(fileId, appId);
  } else {
    // @ts-ignore
    global.TXD_stopDownload(fileId, appId);
  }
};
export const deleteDownload = (fileId: string, appId: string) => {
  if (TurboManager) {
    TurboManager.deleteDownload(fileId, appId);
  } else {
    // @ts-ignore
    global.TXD_deleteDownload(fileId, appId);
  }
};
export const getDownloadList = (): DownloadInfo[] => {
  try {
    if (TurboManager) {
      return TurboManager.getDownloadList();
    } else {
      // @ts-ignore
      return JSON.parse(global.TXD_getDownloadList());
    }
  } catch (e) {
    console.log('----> TxDownloadManager getDownloadList err', e);
    return [];
  }
};

export type DownloadEventType = {
  name: 'progress' | 'finish' | 'start' | 'stop' | 'error';
  fileId: string;
  progress?: number;
  downloaded?: number;
};

type Unsubscribe = () => void;

export const subscribeEvent = (fileId: string, cb: (value: DownloadEventType) => void): Unsubscribe => {
  const emitter = new NativeEventEmitter(NativeModules.TxDownloadManager ? NativeModules.TxDownloadManager : TurboManager);
  const subscription = emitter.addListener('TxDownloadEvent', (value) => {
    console.log('----> download event', value);
    if (value.fileId === fileId) {
      cb && cb(value);
    }
  });
  return () => {
    subscription.remove();
  };
};
