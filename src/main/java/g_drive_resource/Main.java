package g_drive_resource;

import java.io.InputStreamReader;
import java.util.List;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import g_auth_resource.CredentialResource;
import g_auth_resource.ReaderResource;
import g_auth_resource.ScopesResource;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

/**
 * CLI から Google Drive を操作する。
 */
public class Main {
    /**
     * @param args
     *             <ul>
     *             <li>-l, --list: Google Drive 上に存在するファイル・ディレクトリの一覧を表示する。</li>
     *             <li>-c, --create &lt;path&gt;: Google Drive
     *             へファイルまたはディレクトリをアップロードする。</li>
     *             <li>-d, --delete &lt;fileId&gt;: Google Drive
     *             からファイルまたはディレクトリを削除する。</li>
     *             <li>-e, export &lt;path&gt; &lt;fileId&gt;: Google Drive
     *             からファイルまたはディレクトリをダウンロードする。</li>
     *             <li>-p, --parent-id &lt;parentId&gt;: 操作対象とする親ディレクトリの File ID
     *             を指定する。</li>
     *             </ul>
     */
    public static void main(String[] args) {
        LongOpt[] longOpts = new LongOpt[5];
        longOpts[0] = new LongOpt("parent-id", LongOpt.REQUIRED_ARGUMENT, null, 'p');
        longOpts[1] = new LongOpt("list", LongOpt.NO_ARGUMENT, null, 'l');
        longOpts[2] = new LongOpt("create", LongOpt.REQUIRED_ARGUMENT, null, 'c');
        longOpts[3] = new LongOpt("delete", LongOpt.REQUIRED_ARGUMENT, null, 'd');
        longOpts[4] = new LongOpt("export", LongOpt.REQUIRED_ARGUMENT, null, 'e');

        Getopt options = new Getopt("Main", args, "p:lc:d:e:", longOpts);

        int c;
        int parentFlag = 0;
        int listFlag = 0;
        int createFlag = 0;
        int deleteFlag = 0;
        int exportFlag = 0;

        String path = null;
        String parentId = null;
        String fileId = null;

        while ((c = options.getopt()) != -1) {
            switch (c) {
            case 'p':
                parentFlag = 1;
                parentId = options.getOptarg();
                break;
            case 'l':
                listFlag = 1;
                break;
            case 'c':
                createFlag = 1;
                path = options.getOptarg();
                break;
            case 'd':
                deleteFlag = 1;
                fileId = options.getOptarg();
                break;
            case 'e':
                exportFlag = 1;
                path = options.getOptarg();
                fileId = args[options.getOptind()];
            }
        }

        // 操作権限の設定
        ScopesResource sr = new ScopesResource();
        sr.addScope(DriveScopes.DRIVE);
        List<String> scopes = sr.getScopes();

        // 認証ファイルの読込
        ReaderResource rr = new ReaderResource("src/main/resources/credentials.json");
        rr.setEncoding("UTF-8");
        rr.openContext();

        if (rr.getCode() == 1) {
            System.exit(rr.getCode());
        }

        InputStreamReader reader = (InputStreamReader) rr.getContext();

        // 認証実行
        DriveResource dr = new DriveResource(new CredentialResource(reader, scopes), "g-drive-resource");
        rr.closeContext();

        if (dr.getCode() == 1) {
            System.exit(dr.getCode());
        }

        if (parentFlag == 1) {
            dr.setParentId(parentId);
        }

        if (createFlag == 1) {
            dr.create(path);
            System.exit(dr.getCode());
        } else if (deleteFlag == 1) {
            // ファイル削除
            dr.delete(fileId);
            System.exit(dr.getCode());
        } else if (listFlag == 1) {
            // ファイル・ディレクトリの一覧取得。
            List<File> files = null;
            files = dr.getFiles();

            if (dr.getCode() == 1) {
                System.exit(dr.getCode());
            }

            System.out.println("ファイル数: " + files.size());
            System.out.println();

            for (File file : files) {
                System.out.println("Name: " + file.getName());
                System.out.println("ID: " + file.getId());

                List<String> parents = file.getParents();

                for (String parent : parents) {
                    System.out.println("Parent ID: " + parent);
                }

                System.out.println();
            }

            System.exit(2);
        } else if (exportFlag == 1) {
            dr.download(path, fileId);
            System.exit(dr.getCode());
        }
    }
}
