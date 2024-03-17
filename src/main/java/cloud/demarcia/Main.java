package cloud.demarcia;

import cloud.demarcia.downloader.progress.DownloadProgress;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;

import java.util.Scanner;

public class Main {
    public static int num=0;
    public static void main(String[] args) {
        Console.log("Welcome to use Douyin live recorder.");
        String url;
        Recorder recorder=null;
        if (args.length == 0|| StrUtil.isBlank(args[0])) {
            Console.log("No live room url detected, please input it in this console window.");
            Scanner scanner = new Scanner(System.in);
            url = scanner.nextLine();
        }else{
            url=args[0];
            if (args.length > 3) {
                if (args.length==4){
                    Console.log("Downloader started with url:{} start:{} end:{} interval:{}",args[0],args[1],args[2],args[3]);
                    recorder= new Recorder(Integer.parseInt(args[1]),Integer.parseInt(args[2]),Integer.parseInt(args[3]),url);
                }else{
                    Console.log("Downloader started with url:{} start:{} end:{}",args[0],args[1],args[2]);
                    recorder= new Recorder(Integer.parseInt(args[1]),Integer.parseInt(args[2]),url);
                }

            }else{
                Console.log("Downloader started with url:{} ",args[0]);
                recorder= new Recorder(url);
            }
        }
        if(recorder==null) recorder= new Recorder(url);
        recorder.setStopped(true);

        recorder.setProgress(new DownloadProgress() {
            @Override
            public void onStop() {
                Console.log("Recording finished.");
            }

            @Override
            public void onUpdate(long sequence, int sizeMB) {
                for(int i=0;i<num;i++){
                    System.out.print("\r");
                }
                String s="Downloading segment "+sequence+", total size : "+sizeMB+" MB";
                Main.num=s.length();
                System.out.print(s);
            }

            @Override
            public void onParseURLError(String info, String url) {

            }

            @Override
            public void onDownloadTSError(String info, String url, String sequence) {

            }
        });
        recorder.record();

    }
}