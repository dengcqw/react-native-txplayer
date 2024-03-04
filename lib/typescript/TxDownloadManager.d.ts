export interface VideoInfo {
    appId: string;
    fileId: string;
    pSign: string;
}
export interface DownloadInfo {
    dataSource: VideoInfo;
    userName: string;
    duration: number;
    playableDuration: number;
    size: number;
    downloadSize: number;
    segments: number;
    downloadSegments: number;
    progress: number;
    playPath: string;
    speed: number;
    downloadState: number;
    preferredResolution: number;
    isResourceBroken: number;
}
export declare const startDownload: (videoInfo: VideoInfo) => void;
export declare const stopDownload: (fileId: string, appId: string) => void;
export declare const deleteDownload: (fileId: string, appId: string) => void;
export declare const getDownloadList: () => DownloadInfo[];
export type DownloadEventType = {
    name: 'progress' | 'finish' | 'start' | 'stop' | 'error';
    fileId: string;
    progress?: number;
    downloaded?: number;
};
type Unsubscribe = () => void;
export declare const subscribeEvent: (fileId: string, cb: (value: DownloadEventType) => void) => Unsubscribe;
export {};
//# sourceMappingURL=TxDownloadManager.d.ts.map