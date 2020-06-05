package java_itamae_g_drive.app;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java_itamae_contents.domain.model.ContentsAttribute;
import java_itamae_g_auth.domain.model.AuthenticationAttribure;
import java_itamae_g_auth.domain.service.authentication.AuthenticationService;
import java_itamae_g_auth.domain.service.authentication.AuthenticationServiceImpl;
import java_itamae_g_drive.domain.service.drive.DriveService;
import java_itamae_g_drive.domain.service.drive.DriveServiceImpl;

/**
 * CLI から Google Drive を操作する。
 */
public class Main {
    /**
     * @param args
     *            <ul>
     *            <li>-L, --local-path &lt;path&gt;:
     *            操作対象とするローカルファイルまたはディレクトリのパスを指定する。</li>
     *            <li>-R, --remote-file-id &lt;file_id&gt;:
     *            操作対象とするファイルまたはディレクトリの File ID を指定する。</li>
     *            <li>-P, --parent-id &lt;file_id&gt;: 操作対象とする親ディレクトリの File ID
     *            を指定する。</li>
     *            <li>-C, --client-secret &lt;path&gt;: client_secret.json
     *            のパスを指定する。</li>
     *            <li>-E, --encoding &lt;encoding&gt;: client_secret.json
     *            のエンコーディングを指定する。</li>
     *            <li>-l, --list: Google Drive 上に存在するファイル・ディレクトリの一覧を表示する。</li>
     *            <li>-u, --upload: Google Drive へファイル・ディレクトリをアップロードする。</li>
     *            <li>-d, --download: Google Drive からファイル・ディレクトリをダウンロードする。</li>
     *            <li>-m, --modify: Google Drive 上に存在するファイル・ディレクトリを更新する。</li>
     *            <li>-r, --remove: Google Drive からファイル・ディレクトリを削除する。</li>
     *            </ul>
     */
    public static void main(String[] args) {
        // オプションの設定
        LongOpt[] longopts = new LongOpt[10];

        longopts[0] = new LongOpt("local-path", LongOpt.REQUIRED_ARGUMENT, null,
                'L');
        longopts[1] = new LongOpt("remote-file-id", LongOpt.REQUIRED_ARGUMENT,
                null, 'R');
        longopts[2] = new LongOpt("parent-id", LongOpt.REQUIRED_ARGUMENT, null,
                'P');
        longopts[3] = new LongOpt("client-secret", LongOpt.REQUIRED_ARGUMENT,
                null, 'C');
        longopts[4] = new LongOpt("encoding", LongOpt.REQUIRED_ARGUMENT, null,
                'E');
        longopts[5] = new LongOpt("list", LongOpt.NO_ARGUMENT, null, 'l');
        longopts[6] = new LongOpt("upload", LongOpt.NO_ARGUMENT, null, 'u');
        longopts[7] = new LongOpt("download", LongOpt.NO_ARGUMENT, null, 'd');
        longopts[8] = new LongOpt("modify", LongOpt.NO_ARGUMENT, null, 'm');
        longopts[9] = new LongOpt("remove", LongOpt.NO_ARGUMENT, null, 'r');

        int c;

        String clientSecret = "client_secret/client_secret.json";
        String encoding = "UTF-8";

        int localPathFlag = 0;
        String localPath = null;

        int remoteFileIdFlag = 0;
        String remoteFileId = null;

        int parentIdFlag = 0;
        String parentId = null;

        int listFlag = 0;
        int uploadFlag = 0;
        int downloadFlag = 0;
        int modifyFlag = 0;
        int removeFlag = 0;

        // オプションの解析
        Getopt options = new Getopt("Main", args, "L:R:P:C:E:ludmr", longopts);

        while ((c = options.getopt()) != -1) {
            switch (c) {
                case 'L' :
                    localPathFlag = 1;
                    localPath = options.getOptarg();
                    break;
                case 'R' :
                    remoteFileIdFlag = 1;
                    remoteFileId = options.getOptarg();
                    break;
                case 'P' :
                    parentIdFlag = 1;
                    parentId = options.getOptarg();
                    break;
                case 'C' :
                    clientSecret = options.getOptarg();
                    break;
                case 'E' :
                    encoding = options.getOptarg();
                    break;
                case 'l' :
                    listFlag = 1;
                    break;
                case 'u' :
                    uploadFlag = 1;
                    break;
                case 'd' :
                    downloadFlag = 1;
                    break;
                case 'm' :
                    modifyFlag = 1;
                    break;
                case 'r' :
                    removeFlag = 1;
                    break;
            }
        }

        Logger logger = LoggerFactory.getLogger(Main.class);

        // バリデーションチェック
        if (uploadFlag == 1) {
            if (localPathFlag == 0) {
                logger.warn("--local-path オプションを指定してください。");
                System.exit(1);
            }
        }

        if (downloadFlag == 1) {
            if (localPathFlag == 0) {
                logger.warn("--local-path オプションを指定してください。");
                System.exit(1);
            }

            if (remoteFileIdFlag == 0) {
                logger.warn("--remote-file-id オプションを指定してください。");
                System.exit(1);
            }
        }

        if (modifyFlag == 1) {
            if (localPathFlag == 0) {
                logger.warn("--local-path オプションを指定してください。");
                System.exit(1);
            }

            if (remoteFileIdFlag == 0) {
                logger.warn("--remote-file-id オプションを指定してください。");
                System.exit(1);
            }
        }

        if (removeFlag == 1) {
            if (remoteFileIdFlag == 0) {
                logger.warn("--remote-file-id オプションを指定してください。");
                System.exit(1);
            }
        }

        // コマンドの実行
        ContentsAttribute contentsAttr = new ContentsAttribute();
        contentsAttr.setPath(clientSecret);
        contentsAttr.setEncoding(encoding);

        AuthenticationAttribure authAttr;
        AuthenticationService authService;
        DriveService driveService;

        try {
            authAttr = new AuthenticationAttribure();
            authAttr.setUserName("g_drive_user");
            authAttr.addScope(DriveScopes.DRIVE);

            authService = new AuthenticationServiceImpl();
            authService.authorize(contentsAttr, authAttr);

            driveService = new DriveServiceImpl(authAttr);

            if (parentIdFlag == 0) {
                parentId = driveService.getDriveRootId();
            }

            if (listFlag == 1) {
                List<File> fileList = driveService.findByParentId(parentId);
                String format = "id: %s name: %s mime_type: %s";

                for (File file : fileList) {
                    String message = String.format(format, file.getId(),
                            file.getName(), file.getMimeType());
                    System.out.println(message);
                }

                System.exit(0);
            } else if (uploadFlag == 1) {
                driveService.upload(localPath, parentId);
                System.exit(2);
            } else if (downloadFlag == 1) {
                driveService.findByFileId(remoteFileId);
                driveService.download(localPath, remoteFileId);
                System.exit(2);
            } else if (modifyFlag == 1) {
                driveService.update(localPath, remoteFileId);
                System.exit(2);
            } else if (removeFlag == 1) {
                driveService.delete(remoteFileId);
                System.exit(2);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
            System.exit(1);
        }
    }

}
