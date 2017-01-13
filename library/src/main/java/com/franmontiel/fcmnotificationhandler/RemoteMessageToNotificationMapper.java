/*
 * Copyright (C) 2017 Francisco José Montiel Navarro.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.franmontiel.fcmnotificationhandler;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by fj on 12/01/17.
 */
public class RemoteMessageToNotificationMapper {

    private Context context;

    public RemoteMessageToNotificationMapper(Context context) {
        this.context = context;
    }

    /**
     * Map a FCM remote message into a System Notification mimicking the default notification format and behavior applied when the app is in background
     *
     * @param remoteMessage FCM remote message
     * @return The notification builder configured with the notification information of the remote message
     * @throws IllegalArgumentException if the remote message does not contain notification information
     */
    public Notification map(RemoteMessage remoteMessage) throws IllegalArgumentException {
        return mapToNotificationBuilder(remoteMessage).build();
    }

    /**
     * Map a FCM remote message into a System Notification Builder mimicking the default notification format and behavior applied when the app is in background
     *
     * @param remoteMessage FCM remote message
     * @return The notification builder configured with the notification information of the remote message
     * @throws IllegalArgumentException if the remote message does not contain notification information
     */
    public NotificationCompat.Builder mapToNotificationBuilder(RemoteMessage remoteMessage) throws IllegalArgumentException {
        RemoteMessage.Notification fcmNotification = remoteMessage.getNotification();

        if (fcmNotification == null)
            throw new IllegalArgumentException(ErrorMessages.NO_NOTIFICATION_MSG);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(parseTitle(fcmNotification))
                .setContentText(parseBody(fcmNotification))
                .setSmallIcon(parseIcon(fcmNotification))
                .setAutoCancel(true);

        Integer color;
        if ((color = parseColor(fcmNotification)) != null) {
            builder.setColor(color);
        }

        int soundFileResId;
        if (fcmNotification.getSound() != null &&
                !fcmNotification.getSound().equals("default") &&
                (soundFileResId = context.getResources().getIdentifier(fcmNotification.getSound(), "raw", context.getPackageName())) != 0) {

            builder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + soundFileResId));

        } else if (fcmNotification.getSound() != null) {
            builder.setDefaults(Notification.DEFAULT_SOUND);
        }

        builder.setContentIntent(createNotificationPendingItent(remoteMessage));

        return builder;
    }


    private String parseTitle(RemoteMessage.Notification fcmNotification) {
        String title = fcmNotification.getTitle();
        if (title == null) {
            String titleLocalizationKey = fcmNotification.getTitleLocalizationKey();
            if (titleLocalizationKey != null) {
                title = getLocalizedString(titleLocalizationKey, fcmNotification.getTitleLocalizationArgs());
            }
            if (title == null) {
                title = getApplicationName();
            }
        }
        return title;
    }

    @Nullable
    private String parseBody(RemoteMessage.Notification fcmNotification) {
        String body = fcmNotification.getBody();
        if (body == null) {
            String bodyLocalizationKey = fcmNotification.getBodyLocalizationKey();
            if (bodyLocalizationKey != null) {
                body = getLocalizedString(bodyLocalizationKey, fcmNotification.getBodyLocalizationArgs());
            }
        }
        return body;
    }

    private String getLocalizedString(String localizationKey, Object[] localizationArgs) {
        int resId = context.getResources().getIdentifier(localizationKey, "string", context.getPackageName());

        String localizedString = "";
        if (resId != 0)
            localizedString = localizationArgs != null ?
                    context.getString(resId, localizationArgs) :
                    context.getString(resId);

        return localizedString;
    }

    private String getApplicationName() {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    private int parseIcon(RemoteMessage.Notification fcmNotification) {
        String notificationIcon = fcmNotification.getIcon();

        int iconResId = 0;
        if (notificationIcon != null) {
            iconResId = context.getResources().getIdentifier(notificationIcon, "drawable", context.getPackageName());
        }

        if (iconResId == 0) {
            iconResId = getResourceIdFromApplicationMetadata("com.google.firebase.messaging.default_notification_icon");
            if (iconResId == 0)
                iconResId = getApplicationIconResId();
        }
        return iconResId;
    }

    private int getApplicationIconResId() {
        return context.getApplicationInfo().icon;
    }

    @Nullable
    private Integer parseColor(RemoteMessage.Notification fcmNotification) {
        String notificationColor = fcmNotification.getColor();

        Integer color = null;
        if (notificationColor != null) {
            try {
                color = Color.parseColor(notificationColor);
            } catch (IllegalArgumentException ignore) {
            }

            if (color == null) {
                int colorResId = getResourceIdFromApplicationMetadata("com.google.firebase.messaging.default_notification_color");
                if (colorResId != 0)
                    color = ContextCompat.getColor(context, colorResId);
            }
        }
        return color;
    }

    private int getResourceIdFromApplicationMetadata(String metadataName) {
        int resourceId = 0;
        try {
            Bundle appMetadata = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData;
            resourceId = appMetadata.getInt(metadataName, 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return resourceId;
    }

    private int getApplicationAccentColor() {
        TypedArray typedArray = context.obtainStyledAttributes(context.getApplicationInfo().theme, new int[]{R.attr.colorAccent});
        int color = typedArray.getColor(0, 0);
        typedArray.recycle();
        return color;
    }

    private PendingIntent createNotificationPendingItent(RemoteMessage remoteMessage) {
        return PendingIntent.getActivity(context,
                remoteMessage.getMessageId().hashCode(),
                createNotificationIntent(remoteMessage),
                PendingIntent.FLAG_ONE_SHOT);
    }

    private Intent createNotificationIntent(RemoteMessage remoteMessage) {
        RemoteMessage.Notification fcmNotification = remoteMessage.getNotification();

        Intent intent = null;
        if (fcmNotification.getClickAction() != null) {
            intent = new Intent(fcmNotification.getClickAction());
        }
        if (intent == null) {
            intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        }

        Map<String, String> data = remoteMessage.getData();
        if (data != null) {
            for (String key : data.keySet()) {
                intent.putExtra(key, data.get(key));
            }
        }
        intent.putExtra("google.sent_time", String.valueOf(remoteMessage.getSentTime()));
        intent.putExtra("from", remoteMessage.getFrom());
        intent.putExtra("google.message_id", remoteMessage.getMessageId());
        intent.putExtra("collapse_key", remoteMessage.getCollapseKey());

        return intent;
    }
}