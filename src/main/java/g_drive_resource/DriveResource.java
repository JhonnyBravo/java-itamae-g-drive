package g_drive_resource;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import g_auth_resource.ConnectionResource;
import status_resource.Status;

/**
 * Google Drive の操作を管理する。
 */
public class DriveResource extends Status {
    private Drive drive;
    private String parentId = null;

    /**
     * @param connection 操作対象とする ConnectionResource を指定する。
     * @param appName    アプリケーションの名前を指定する。
     */
    public DriveResource(ConnectionResource connection, String appName) {
        this.initStatus();

        this.drive = new Drive.Builder(connection.getTransport(), connection.getJsonFactory(),
                connection.getCredential()).setApplicationName(appName).build();

        this.setCode(connection.getCode());
    }

    /**
     * @param parentId 親ディレクトリのファイル ID を指定する。
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * @param fileId 取得対象とするファイルまたはディレクトリのファイル ID を指定する。
     * @return file Google Drive 上に存在するファイルまたはディレクトリを返す。
     */
    public File getFileById(String fileId) {
        this.initStatus();
        File file = null;

        System.out.println(fileId + " を取得しています。");

        try {
            file = this.drive.files().get(fileId).setFields("id, name, parents, mimeType").execute();
            this.setCode(2);
        } catch (IOException e) {
            this.errorTerminate("エラーが発生しました。 " + e);
        }

        return file;
    }

    /**
     * @return files Google Drive 上に存在するファイルまたはディレクトリの一覧を返す。
     */
    public List<File> getFiles() {
        this.initStatus();
        List<File> files = null;

        System.out.println("ファイル一覧を取得しています。");

        try {
            if (this.parentId != null) {
                files = this.drive.files().list().setFields("files(id, name, parents)")
                        .setQ("'" + this.parentId + "' in parents").execute().getFiles();
            } else {
                files = this.drive.files().list().setFields("files(id, name, parents)").execute().getFiles();
            }

            if (files.size() > 0) {
                this.setCode(2);
            }
        } catch (IOException e) {
            this.errorTerminate("エラーが発生しました。 " + e);
        }

        return files;
    }

    /**
     * Google Drive へローカルのファイルまたはディレクトリをアップロードする。
     * 
     * @param path アップロード対象とするファイルまたはディレクトリのパスを指定する。
     * @return file アップロードしたファイルまたはディレクトリを返す。
     */
    public File create(String path) {
        this.initStatus();
        File result = null;

        File remote = new File();
        java.io.File local = new java.io.File(path);

        if (!local.exists()) {
            this.errorTerminate(path + " が見つかりません。");
            return result;
        }

        if (this.parentId != null) {
            List<String> parents = new ArrayList<String>();
            parents.add(this.parentId);
            remote.setParents(parents);
        }

        remote.setName(local.getName());
        System.out.println(path + " をアップロードしています。");

        try {
            if (local.isDirectory()) {
                remote.setMimeType("application/vnd.google-apps.folder");
                result = this.drive.files().create(remote).setFields("id, name, mimeType, parents").execute();
                this.setCode(2);
            } else {
                FileContent content = new FileContent(null, local);
                result = this.drive.files().create(remote, content).setFields("id, name, mimeType, parents").execute();
                this.setCode(2);
            }
        } catch (IOException e) {
            this.errorTerminate("エラーが発生しました。 " + e);
        }

        return result;
    }

    /**
     * Google Drive 上に存在するファイルまたはディレクトリを削除する。
     * 
     * @param fileId 削除対象とするファイルまたはディレクトリのファイル ID を指定する。
     */
    public void delete(String fileId) {
        this.initStatus();

        System.out.println(fileId + " を削除しています。");

        try {
            this.drive.files().delete(fileId).execute();
            this.setCode(2);
        } catch (IOException e) {
            this.errorTerminate("エラーが発生しました。 " + e);
        }
    }

    /**
     * Google Drive 上に存在するファイルまたはディレクトリをローカルへダウンロードする。
     * 
     * @param path   ダウンロード先ディレクトリのパスを指定する。
     * @param fileId ダウンロード対象とするファイルまたはディレクトリのファイル ID を指定する。
     * @return file ダウンロードしたファイルまたはディレクトリを返す。
     */
    public java.io.File download(String path, String fileId) {
        this.initStatus();

        java.io.File local = null;
        File remote = null;

        System.out.println(fileId + " をダウンロードしています。");

        try {
            remote = this.drive.files().get(fileId).execute();
            local = new java.io.File(path, remote.getName());

            if (remote.getMimeType().equals("application/vnd.google-apps.folder")) {
                local.mkdirs();
                this.setCode(2);
            } else {
                this.drive.files().get(fileId).executeMediaAndDownloadTo(new FileOutputStream(local));
                this.setCode(2);
            }
        } catch (IOException e) {
            this.errorTerminate("エラーが発生しました。 " + e);
        }

        return local;
    }
}
