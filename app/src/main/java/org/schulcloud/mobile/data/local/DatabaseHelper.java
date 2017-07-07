package org.schulcloud.mobile.data.local;

import android.util.Log;

import org.schulcloud.mobile.data.model.AccessToken;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Directory;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.model.File;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.data.model.Topic;
import org.schulcloud.mobile.data.model.User;
import org.schulcloud.mobile.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import io.realm.Realm;
import rx.Observable;
import timber.log.Timber;

@Singleton
public class DatabaseHelper {

    private final Provider<Realm> mRealmProvider;

    @Inject
    DatabaseHelper(Provider<Realm> realmProvider) {
        mRealmProvider = realmProvider;
    }

    public void clearTable(Class table) {
        final Realm realm = mRealmProvider.get();
        realm.executeTransaction(realm1 -> realm1.delete(table));
    }

    /**** Users ****/

    public Observable<User> setUsers(final Collection<User> newUsers) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newUsers));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        });
    }

    public Observable<List<User>> getUsers() {
        final Realm realm = mRealmProvider.get();
        return realm.where(User.class).findAllAsync().asObservable()
                .filter(users -> users.isLoaded())
                .map(users -> realm.copyFromRealm(users));
    }

    public Observable<AccessToken> setAccessToken(final AccessToken newAccessToken) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newAccessToken));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    subscriber.onCompleted();
                    realm.close();
                }
            }
        });
    }

    public Observable<AccessToken> getAccessToken() {
        final Realm realm = mRealmProvider.get();
        return Observable.just(realm.where(AccessToken.class).findFirstAsync());
    }

    public Observable<CurrentUser> setCurrentUser(final CurrentUser currentUser) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(currentUser));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    subscriber.onCompleted();
                    realm.close();
                }
            }
        });
    }

    public Observable<CurrentUser> getCurrentUser() {
        final Realm realm = mRealmProvider.get();
        return Observable.just(realm.where(CurrentUser.class).findFirstAsync());
    }


    /**** FileStorage ****/

    public Observable<File> setFiles(final Collection<File> files) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(files));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    subscriber.onCompleted();
                    realm.close();
                }
            }
        });
    }

    public Observable<List<File>> getFiles() {
        final Realm realm = mRealmProvider.get();
        return realm.where(File.class).findAllAsync().asObservable()
                .filter(files -> files.isLoaded())
                .map(files -> realm.copyFromRealm(files));
    }

    public Observable<Directory> setDirectories(final Collection<Directory> directories) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(directories));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    subscriber.onCompleted();
                    realm.close();
                }
            }
        });
    }

    public Observable<List<Directory>> getDirectories() {
        final Realm realm = mRealmProvider.get();
        return realm.where(Directory.class).findAllAsync().asObservable()
                .filter(directories -> directories.isLoaded())
                .map(directories -> realm.copyFromRealm(directories));
    }

    /**** Events ****/
    public Observable<Event> setEvents(final Collection<Event> events) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(events));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    subscriber.onCompleted();
                    realm.close();
                }
            }
        });
    }

    public Observable<List<Event>> getEvents() {
        final Realm realm = mRealmProvider.get();
        return realm.where(Event.class).findAllAsync().asObservable()
                .filter(events -> events.isLoaded())
                .map(events -> realm.copyFromRealm(events));
    }

    public List<Event> getEventsForDay() {
        final Realm realm = mRealmProvider.get();
        Collection<Event> events = realm.where(Event.class).findAll();

        int weekday = new GregorianCalendar().get(Calendar.DAY_OF_WEEK);
        Log.d("Weekday", Integer.toString(weekday));

        List<Event> eventsForWeekday = new ArrayList<>();

        for (Event event : events) {
            if (event.included.size() > 0) {
                if (getNumberForWeekday(event.included.first().getAttributes().getWkst()) == weekday) {
                    eventsForWeekday.add(event);
                }
            }
        }

        Collections.sort(eventsForWeekday, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                return o1.start.compareTo(o2.start);
            }
        });

        return eventsForWeekday;
    }

    public int getNumberForWeekday(String weekday) {
        switch (weekday) {
            case "SU":
                return 1;
            case "MO":
                return 2;
            case "TU":
                return 3;
            case "WE":
                return 4;
            case "TH":
                return 5;
            case "FR":
                return 6;
            case "SA":
                return 7;
            default:
                return -1;
        }
    }

    /**** NotificationService ****/

    public Observable<Device> setDevices(final Collection<Device> newDevices) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newDevices));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        });
    }

    public Observable<List<Device>> getDevices() {
        final Realm realm = mRealmProvider.get();
        return realm.where(Device.class).findAllAsync().asObservable()
                .filter(devices -> devices.isLoaded())
                .map(devices -> realm.copyFromRealm(devices));
    }

    /**** Homework ****/

    public Observable<Homework> setHomework(final Collection<Homework> newHomework) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newHomework));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        });
    }

    public Observable<List<Homework>> getHomework() {
        final Realm realm = mRealmProvider.get();
        return realm.where(Homework.class).findAllAsync().asObservable()
                .filter(homework -> homework.isLoaded())
                .map(homework -> realm.copyFromRealm(homework));
    }

    public Homework getHomeworkForId(String homeworkId) {
        final Realm realm = mRealmProvider.get();
        return realm.where(Homework.class).equalTo("_id", homeworkId).findFirst();
    }

    public Pair<String, String> getOpenHomeworks() {
        final Realm realm = mRealmProvider.get();
        Collection<Homework> homeworks = realm.where(Homework.class).findAll();

        Date nextDueDate = null;
        int amount = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

        Calendar calendar = new GregorianCalendar(9999, 12, 31, 23, 59);
        try {
            nextDueDate = dateFormat.parse(dateFormat.format(calendar.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (Homework homework : homeworks) {
            Date untilDate = null;
            try {
                untilDate = dateFormat.parse(homework.dueDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (untilDate.after(new Date())) {
                amount++;

                if (untilDate.before(nextDueDate))
                    nextDueDate = untilDate;
            }
        }

        return new Pair<String, String>(Integer.toString(amount), dateFormat.format(nextDueDate));
    }

    /**** Submissions ****/
    public Observable<Submission> setSubmissions(final Collection<Submission> newSubmission) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newSubmission));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        });
    }

    public Observable<List<Submission>> getSubmissions() {
        final Realm realm = mRealmProvider.get();
        return realm.where(Submission.class).findAllAsync().asObservable()
                .filter(submission -> submission.isLoaded())
                .map(submission -> realm.copyFromRealm(submission));
    }

    public Submission getSubmissionForId(String homeworkId) {
        final Realm realm = mRealmProvider.get();
        return realm.where(Submission.class).equalTo("homeworkId", homeworkId).findFirst();
    }

    /**** Courses ****/

    public Observable<Course> setCourses(final Collection<Course> newCourse) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newCourse));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        });
    }

    public Observable<List<Course>> getCourses() {
        final Realm realm = mRealmProvider.get();
        return realm.where(Course.class).findAllAsync().asObservable()
                .filter(course -> course.isLoaded())
                .map(course -> realm.copyFromRealm(course));
    }

    public Course getCourseForId(String courseId) {
        final Realm realm = mRealmProvider.get();
        return realm.where(Course.class).equalTo("_id", courseId).findFirst();
    }

    /**** Topics ****/

    public Observable<Topic> setTopics(final Collection<Topic> newTopic) {
        return Observable.create(subscriber -> {
            if (subscriber.isUnsubscribed()) return;
            Realm realm = null;

            try {
                realm = mRealmProvider.get();
                realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(newTopic));
            } catch (Exception e) {
                Timber.e(e, "There was an error while adding in Realm.");
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        });
    }

    public Observable<List<Topic>> getTopics() {
        final Realm realm = mRealmProvider.get();
        return realm.where(Topic.class).findAllAsync().asObservable()
                .filter(topic -> topic.isLoaded())
                .map(topic -> realm.copyFromRealm(topic));
    }

    public Topic getContents(String topicId) {
        final Realm realm = mRealmProvider.get();
        return realm.where(Topic.class).equalTo("_id", topicId).findFirst();
    }
}
