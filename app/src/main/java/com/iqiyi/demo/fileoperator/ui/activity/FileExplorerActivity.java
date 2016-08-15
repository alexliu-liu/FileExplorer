/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iqiyi.demo.fileoperator.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.widget.Toast;

import com.iqiyi.demo.fileoperator.event.FileExplorerEvents;
import com.iqiyi.demo.fileoperator.ui.fragment.FileListFragment;
import com.iqiyi.demo.fileoperator.R;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;


public class FileExplorerActivity extends AppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doOpenDirectory("/", false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        FileExplorerEvents.getBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        FileExplorerEvents.getBus().unregister(this);
    }

    private void doOpenDirectory(String path, boolean addToBackStack) {
        Fragment newFragment = FileListFragment.newInstance(path);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.body, newFragment);

        if (addToBackStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    @Subscribe
    public void onClickFile(FileExplorerEvents.OnClickFile event) {
        File f = event.mFile;
        try {
            f = f.getAbsoluteFile();
            f = f.getCanonicalFile();
            if (TextUtils.isEmpty(f.toString()))
                f = new File("/");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (f.isDirectory()) {
            String path = f.toString();
            Toast.makeText(this, "Enter path : \n" + f.toString(), Toast.LENGTH_LONG).show();
            doOpenDirectory(path, true);
        } else if (f.exists()) {
            Toast.makeText(this, "Path : \n" + f.getPath() + "\n" + f.getName(), Toast.LENGTH_LONG).show();
        }
    }
}
