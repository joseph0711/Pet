package com.example.pet.ui.feed;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.pet.ConnectionMysqlClass;
import com.example.pet.R;
import com.example.pet.SharedViewModel;
import com.example.pet.UserClass;
import com.example.pet.databinding.FragmentFeedBinding;
import com.example.pet.ui.feedAutomatic.FeedAutomaticFragment;
import com.example.pet.ui.feedManual.FeedManualFragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FeedFragment extends Fragment {
    private FragmentFeedBinding binding;
    ConnectionMysqlClass connectionMysqlClass;
    Connection con;
    String str;
    private ImageView petImageView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        petImageView = root.findViewById(R.id.feed_imgAvatarPet);

        connectionMysqlClass = new ConnectionMysqlClass();
        connect(this::findPetAvatarById);

        Button btnManual = root.findViewById(R.id.feed_btnManual);
        Button btnAutomatic = root.findViewById(R.id.feed_btnAutomatic);

        btnManual.setOnClickListener(view -> {
            Fragment fragment = new FeedManualFragment();
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment).commit();
        });
        btnAutomatic.setOnClickListener(view -> {
            Fragment fragment = new FeedAutomaticFragment();
            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment).commit();
        });
        return root;
    }

    private void findPetAvatarById() {
        String sql = "SELECT pet.PetImage as PetImage FROM user JOIN pet ON user.id = pet.id WHERE user.id = ?;";

        // Get the SharedViewModel instance
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Get the UserClass object from the SharedViewModel
        UserClass userClass = sharedViewModel.getUserClass().getValue();

        // Get the id from the UserClass object
        int id = userClass.getId();

        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setInt(1, id);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    // Retrieve the image data.
                    byte[] petImageBytes = resultSet.getBytes("PetImage");

                    // Convert the byte arrays to Bitmaps.
                    Bitmap petBitmap = BitmapFactory.decodeByteArray(petImageBytes, 0, petImageBytes.length);

                    // Set the Bitmaps to ImageViews.
                    requireActivity().runOnUiThread(() -> {
                        if (petImageView != null) {
                            petImageView.setImageBitmap(petBitmap);
                        }
                    });
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void connect(Runnable onConnected) {
        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                con = connectionMysqlClass.CONN();
                if (con == null) {
                    requireActivity().runOnUiThread(() -> {
                        str = "Error in connection with SQL server";
                        Toast.makeText(requireActivity(), str, Toast.LENGTH_SHORT).show();
                    });
                } else {
                    requireActivity().runOnUiThread(onConnected);
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}