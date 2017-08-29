package org.schulcloud.mobile.ui.layouts;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.schulcloud.mobile.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Layout for creating a list item in Material Design. Children must have the attribute {@link
 * org.schulcloud.mobile.R.styleable#ListItemLayout_Layout_layout_position
 * layout_position="&lt;position&gt;"} in order to be displayed.
 * <p>
 * Supported features are:
 * <ul>
 * <li>Primary text, Secondary text and Secondary2 text (3rd line)</li>
 * <li>Start icon or Avatar</li>
 * <li>End icon or End text(s)</li>
 * </ul>
 * <p>
 * <b>How displayed elements are selected:</b>
 * For every possible position, only the last added element is considered. After that, any child
 * with <code>visibility="gone"</code> is ignored. An avatar takes precedence over a start icon,
 * and an end icon takes precedence over any end texts.<br />
 * <b>Example:</b> If an avatar is defined with <code>visibility="visible"</code>, any start icon
 * is ignored. The same happens with <code>visibility="invisible"</code>. But with
 * <code>visibility="gone"</code>, the avatar is ignored and if there is a start icon, it is used
 * instead.<br />
 * <b>Note:</b> If there is no secondary, but secondary2 text, it will be
 * <p>
 * The default styles for all texts are:
 * <ul>
 * <li>Primary text: {@link org.schulcloud.mobile.R.style#Material_ListItem_TextPrimary}</li>
 * <li>Secondary text: {@link org.schulcloud.mobile.R.style#Material_ListItem_TextSecondary}</li>
 * <li>Secondary2 text: {@link org.schulcloud.mobile.R.style#Material_ListItem_TextSecondary2}</li>
 * <li>End text (top): {@link org.schulcloud.mobile.R.style#Material_ListItem_EndTextTop}</li>
 * <li>End text (bottom): {@link org.schulcloud.mobile.R.style#Material_ListItem_EndTextBottom}</li>
 * </ul>
 * <p>
 * If you just have the primary text, style your {@link TextView} with {@link
 * org.schulcloud.mobile.R.style#Material_ListItem_Simple} instead of using ListItemLayout.
 *
 * @see org.schulcloud.mobile.R.style#Material_ListItem_Simple
 */
public final class ListItemLayout extends ViewGroup {
    private View mChildAvatar = null;
    private View mChildStartIcon = null;
    private View mChildTextPrimary = null;
    private View mChildTextSecondary = null;
    private View mChildTextSecondary2 = null;
    private View mChildEndIcon = null;
    private View mChildEndTextTop = null;
    private View mChildEndTextBottom = null;

    private View[] mChildsMainText;
    private int[] mHeightMeasureSpecsMainText;

    private int mAvatar_measureSpec;
    private int mStartIcon_measureSpec;
    private int mEndIcon_measureSpec;
    private int mEndText_measureSpecWidth;
    private int mEndText_measureSpecHeight;

    public ListItemLayout(@NonNull Context context) {
        this(context, null);
    }
    public ListItemLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ListItemLayout(@NonNull Context context, @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }
    @TargetApi(21)
    public ListItemLayout(@NonNull Context context, @Nullable AttributeSet attrs,
            @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }
    private void init(@Nullable AttributeSet attrs, @AttrRes int defStyleAttr,
            @StyleRes int defStyleRes) {
        Resources res = getResources();
        mAvatar_measureSpec = MeasureSpec.makeMeasureSpec(
                res.getDimensionPixelSize(R.dimen.material_listItemLayout_avatar_size),
                MeasureSpec.EXACTLY);
        mStartIcon_measureSpec = MeasureSpec.makeMeasureSpec(
                res.getDimensionPixelSize(
                        R.dimen.material_listItemLayout_startIcon_size),
                MeasureSpec.EXACTLY);

        mChildsMainText = new View[]{null, null, null};
        mHeightMeasureSpecsMainText = new int[]{
                MeasureSpec.makeMeasureSpec(
                        res.getDimensionPixelSize(
                                R.dimen.material_listItemLayout_textPrimary_textSize),
                        MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(
                        res.getDimensionPixelSize(
                                R.dimen.material_listItemLayout_textSecondary_textSize),
                        MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(
                        res.getDimensionPixelSize(
                                R.dimen.material_listItemLayout_textSecondary2_textSize),
                        MeasureSpec.EXACTLY)};

        mEndIcon_measureSpec = MeasureSpec.makeMeasureSpec(
                res.getDimensionPixelSize(
                        R.dimen.material_listItemLayout_endIcon_size),
                MeasureSpec.EXACTLY);

        mEndText_measureSpecWidth = MeasureSpec.makeMeasureSpec(
                res.getDimensionPixelSize(R.dimen.material_listItemLayout_endText_width),
                MeasureSpec.EXACTLY);
        mEndText_measureSpecHeight = MeasureSpec.makeMeasureSpec(
                res.getDimensionPixelSize(
                        R.dimen.material_listItemLayout_endText_textSize),
                MeasureSpec.EXACTLY);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }
    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Fix if textSecondary2 is set, but textSecondary isn't
        if (mChildTextSecondary == null && mChildTextSecondary2 != null) {
            mChildTextSecondary = mChildTextSecondary2;
            mChildTextSecondary2 = null;
        }

        Resources res = getResources();
        int totalExtrasWidth = getTotalHorizontalPadding();

        // Set required height
        int heightRes;
        if (!takesSpace(mChildTextSecondary2))
            if (!takesSpace(mChildTextSecondary))
                if (!takesSpace(mChildAvatar))
                    heightRes = R.dimen.material_listItemLayout_height;
                else
                    heightRes = R.dimen.material_listItemLayout_height_singleLineWithAvatar;
            else
                heightRes = R.dimen.material_listItemLayout_height_twoLine;
        else
            heightRes = R.dimen.material_listItemLayout_height_threeLine;
        int height = getResources().getDimensionPixelSize(heightRes)
                + getPaddingTop() + getPaddingBottom();

        // Avatar
        int text_offsetStart_withStartDetail = res.getDimensionPixelSize(
                R.dimen.material_listItemLayout_text_offsetStart_withStartDetail);
        if (takesSpace(mChildAvatar)) {
            totalExtrasWidth += text_offsetStart_withStartDetail;
            measureChild(mChildAvatar, mAvatar_measureSpec, mAvatar_measureSpec);
        } else if (takesSpace(mChildStartIcon)) {
            totalExtrasWidth += text_offsetStart_withStartDetail;
            measureChild(mChildStartIcon, mStartIcon_measureSpec, mStartIcon_measureSpec);
        }

        // End icon/end texts
        if (takesSpace(mChildEndIcon)) {
            totalExtrasWidth += res.getDimensionPixelSize(
                    R.dimen.material_listItemLayout_text_offsetEnd_withEndIcon);
            measureChild(mChildEndIcon, mEndIcon_measureSpec, mEndIcon_measureSpec);
        } else if (takesSpace(mChildEndTextTop) || takesSpace(mChildEndTextBottom)) {
            totalExtrasWidth += res.getDimensionPixelOffset(
                    R.dimen.material_listItemLayout_text_offsetEnd_withEndText);
            if (takesSpace(mChildEndTextTop))
                mChildEndTextTop.measure(mEndText_measureSpecWidth, mEndText_measureSpecHeight);
            if (takesSpace(mChildEndTextBottom))
                mChildEndTextBottom.measure(mEndText_measureSpecWidth, mEndText_measureSpecHeight);
        }

        // Main texts
        int combinedTextWidthAndState = measureCombinedMainTextViewsWidthAndState(
                MeasureSpec.getSize(widthMeasureSpec) - totalExtrasWidth,
                MeasureSpec.getMode(widthMeasureSpec));
        int combinedChildMeasuredStates = combinedTextWidthAndState & View.MEASURED_STATE_MASK;
        int maxRequiredTextWidth = combinedTextWidthAndState & View.MEASURED_SIZE_MASK;

        setMeasuredDimension(
                resolveSizeAndState(totalExtrasWidth + maxRequiredTextWidth,
                        widthMeasureSpec, combinedChildMeasuredStates),
                resolveSizeAndState(height, heightMeasureSpec, 0));
    }
    private int getTotalHorizontalPadding() {
        return getPaddingLeft() + getPaddingRight() + 2 * getResources().getDimensionPixelOffset(
                R.dimen.material_listItemLayout_paddingHorizontal);
    }
    private int measureCombinedMainTextViewsWidthAndState(int maxWidth, int widthMode) {
        int combinedChildMeasuredStates = 0;
        int maxRequiredTextWidth = 0;

        for (int i = 0; i < mChildsMainText.length; i++) {
            View textView = mChildsMainText[i];
            if (!takesSpace(textView))
                continue;

            measureChild(textView, MeasureSpec.makeMeasureSpec(maxWidth, widthMode),
                    mHeightMeasureSpecsMainText[i]);
            int measuredWidthAndState = textView.getMeasuredWidthAndState();

            combinedChildMeasuredStates = combineMeasuredStates(combinedChildMeasuredStates,
                    measuredWidthAndState);
            maxRequiredTextWidth = Math.max(maxRequiredTextWidth,
                    measuredWidthAndState & View.MEASURED_SIZE_MASK);
        }
        return (combinedChildMeasuredStates & View.MEASURED_STATE_MASK) | maxRequiredTextWidth;
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingHorizontal = getResources().getDimensionPixelSize(
                R.dimen.material_listItemLayout_paddingHorizontal);
        int left = getPaddingLeft() + paddingHorizontal;
        int top = getPaddingTop();
        int right = getMeasuredWidth() - getPaddingRight() - paddingHorizontal;
        int bottom = getMeasuredHeight() - getPaddingBottom();

        int textLeft = left;
        int textRight = right;

        Resources res = getResources();
        int avatar_size = res.getDimensionPixelSize(
                R.dimen.material_listItemLayout_avatar_size);
        int startIcon_size = res.getDimensionPixelSize(
                R.dimen.material_listItemLayout_startIcon_size);
        int startDetail_marginTop_withSecondary2 = res.getDimensionPixelSize(
                R.dimen.material_listItemLayout_startDetail_marginTop_withSecondary2);

        int text_offsetTop = res.getDimensionPixelSize(
                R.dimen.material_listItemLayout_text_offsetTop);
        int text_offsetTop_withSecondary2 = res.getDimensionPixelSize(
                R.dimen.material_listItemLayout_text_offsetTop_withSecondary2);
        int text_offsetBottom = res.getDimensionPixelSize(
                R.dimen.material_listItemLayout_text_offsetBottom);
        int text_offsetStart_withStartDetail = res.getDimensionPixelSize(
                R.dimen.material_listItemLayout_text_offsetStart_withStartDetail);
        int text_offsetEnd_withEndIcon = res.getDimensionPixelSize(
                R.dimen.material_listItemLayout_text_offsetEnd_withEndIcon);
        int text_offsetEnd_withEndText = res.getDimensionPixelSize(
                R.dimen.material_listItemLayout_text_offsetEnd_withEndText);
        int textPrimary_textSize = res.getDimensionPixelSize(
                R.dimen.material_listItemLayout_textPrimary_textSize);
        int textSecondary_textSize = res.getDimensionPixelSize(
                R.dimen.material_listItemLayout_textSecondary_textSize);
        int textSecondary2_textSize = res.getDimensionPixelSize(
                R.dimen.material_listItemLayout_textSecondary2_textSize);

        int endIcon_size = res.getDimensionPixelSize(
                R.dimen.material_listItemLayout_endIcon_size);
        int endIcon_marginTop_withSecondary2 = res.getDimensionPixelSize(
                R.dimen.material_listItemLayout_endIcon_marginTop_withSecondary2);
        int endText_width = res.getDimensionPixelSize(
                R.dimen.material_listItemLayout_endText_width);
        int endText_textSize = res.getDimensionPixelSize(
                R.dimen.material_listItemLayout_endText_textSize);

        // Start icon/avatar
        if (takesSpace(mChildAvatar)) {
            textLeft += text_offsetStart_withStartDetail;
            int avatar_top = !takesSpace(mChildTextSecondary2)
                    ? (bottom - top - avatar_size) / 2
                    : startDetail_marginTop_withSecondary2;
            mChildAvatar.layout(left, avatar_top, left + avatar_size,
                    avatar_top + avatar_size);
        } else if (takesSpace(mChildStartIcon)) {
            textLeft += text_offsetStart_withStartDetail;
            int startIcon_top = !takesSpace(mChildTextSecondary2)
                    ? (bottom - top - startIcon_size) / 2
                    : startDetail_marginTop_withSecondary2;
            mChildStartIcon.layout(left, startIcon_top, left + startIcon_size,
                    startIcon_top + startIcon_size);
        }

        // End icon/end texts
        if (takesSpace(mChildEndIcon)) {
            int endIcon_top = !takesSpace(mChildTextSecondary2)
                    ? (bottom - top - endIcon_size) / 2
                    : endIcon_marginTop_withSecondary2;
            textRight -= text_offsetEnd_withEndIcon;
            mChildEndIcon.layout(right - endIcon_size, endIcon_top, right,
                    endIcon_top + endIcon_size);
        } else if (takesSpace(mChildEndTextTop) || takesSpace(mChildEndTextBottom)) {
            textRight -= text_offsetEnd_withEndText;
            int endText_marginVertical;
            if (!takesSpace(mChildTextSecondary2))
                if (!takesSpace(mChildTextSecondary))
                    endText_marginVertical = res.getDimensionPixelSize(
                            R.dimen.material_listItemLayout_endText_marginVertical);
                else
                    endText_marginVertical = res.getDimensionPixelSize(
                            R.dimen.material_listItemLayout_endText_marginVertical_withSecondary);
            else
                endText_marginVertical = res.getDimensionPixelSize(
                        R.dimen.material_listItemLayout_endText_marginVertical_withSecondary2);
            if (takesSpace(mChildEndTextTop))
                mChildEndTextTop.layout(right - endText_width, top + endText_marginVertical, right,
                        top + endText_marginVertical + getRequiredTextHeight(mChildEndTextTop));
            if (takesSpace(mChildEndTextBottom)) {
                int endTextBottom_top = bottom - endText_marginVertical - endText_textSize;
                mChildEndTextBottom.layout(right - endText_width, endTextBottom_top, right,
                        endTextBottom_top + getRequiredTextHeight(mChildEndTextBottom));
            }
        }

        // Primary text
        int textPrimary_top;
        if (!takesSpace(mChildTextSecondary2))
            if (!takesSpace(mChildTextSecondary))
                textPrimary_top = (bottom - top - textPrimary_textSize) / 2;
            else
                textPrimary_top = top + text_offsetTop;
        else
            textPrimary_top = text_offsetTop_withSecondary2;

        if (takesSpace(mChildTextPrimary))
            mChildTextPrimary.layout(textLeft, textPrimary_top, textRight,
                    textPrimary_top + getRequiredTextHeight(mChildTextPrimary));

        // Secondary2 text
        int textSecondary2_top = bottom - text_offsetBottom - textSecondary2_textSize;
        if (takesSpace(mChildTextSecondary2))
            mChildTextSecondary2.layout(textLeft, textSecondary2_top, textRight,
                    textSecondary2_top + getRequiredTextHeight(mChildTextSecondary2));

        // Secondary text
        if (takesSpace(mChildTextSecondary)) {
            int textSecondaryTop = !takesSpace(mChildTextSecondary2)
                    ? bottom - text_offsetBottom - textSecondary_textSize
                    : (textPrimary_top + textPrimary_textSize + textSecondary2_top
                            - textSecondary_textSize) / 2;
            mChildTextSecondary.layout(textLeft, textSecondaryTop, textRight,
                    textSecondaryTop + getRequiredTextHeight(mChildTextSecondary));
        }
    }
    private int getRequiredTextHeight(@NonNull View child) {
        return child instanceof TextView
                ? ((TextView) child).getLineHeight()
                : child.getMeasuredHeight();
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);

        // Assign child view to its specified position
        setChildAtPosition(child, ((LayoutParams) child.getLayoutParams()).position);

        if (child instanceof TextView)
            ((TextView) child).setIncludeFontPadding(false);
    }
    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);

        reassignPosition(((LayoutParams) child.getLayoutParams()).position);
    }

    /**
     * Assigns the last added child with the specified position to this position.
     *
     * @param position The position to reassign.
     * @return True if a child with this position was found, false otherwise
     */
    private boolean reassignPosition(@LayoutParams.Position int position) {
        int count = getChildCount();
        for (int i = count - 1; i >= 0; i++) {
            View child = getChildAt(i);
            if (((LayoutParams) child.getLayoutParams()).position == position) {
                setChildAtPosition(child, position);
                return true;
            }
        }
        setChildAtPosition(null, position);
        return false;
    }
    /**
     * Assigns the specified child to the specified position.
     *
     * @param child    The child to place
     * @param position The position to place the child at
     */
    private void setChildAtPosition(@Nullable View child, int position) {
        switch (position) {
            case LayoutParams.POSITION_AVATAR:
                mChildAvatar = child;
                break;
            case LayoutParams.POSITION_START_ICON:
                mChildStartIcon = child;
                break;
            case LayoutParams.POSITION_TEXT_PRIMARY:
                mChildTextPrimary = child;
                mChildsMainText[0] = child;
                break;
            case LayoutParams.POSITION_TEXT_SECONDARY:
                mChildTextSecondary = child;
                mChildsMainText[1] = child;
                break;
            case LayoutParams.POSITION_TEXT_SECONDARY_2:
                mChildTextSecondary2 = child;
                mChildsMainText[2] = child;
                break;
            case LayoutParams.POSITION_END_ICON:
                mChildEndIcon = child;
                break;
            case LayoutParams.POSITION_END_TEXT_TOP:
                mChildEndTextTop = child;
                break;
            case LayoutParams.POSITION_END_TEXT_BOTTOM:
                mChildEndTextBottom = child;
                break;
        }
    }
    /**
     * Checks whether the child is set and takes space during layout (<code>visibility !=
     * GONE</code>).
     *
     * @param child The {@link View} to check.
     * @return True if the child is set and takes space, false otherwise.
     */
    private static boolean takesSpace(@Nullable View child) {
        return child != null && child.getVisibility() != GONE;
    }

    /**
     * Per-child layout information associated with {@link ListItemLayout}.
     *
     * @see org.schulcloud.mobile.R.styleable#ListItemLayout_Layout_layout_position
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public static class LayoutParams extends ViewGroup.LayoutParams {
        @IntDef({POSITION_UNSPECIFIED, POSITION_AVATAR, POSITION_START_ICON,
                POSITION_TEXT_PRIMARY, POSITION_TEXT_SECONDARY, POSITION_TEXT_SECONDARY_2,
                POSITION_END_ICON})
        @Retention(RetentionPolicy.SOURCE)
        public @interface Position {}

        public static final int POSITION_UNSPECIFIED = -1;
        public static final int POSITION_AVATAR = 0;
        public static final int POSITION_START_ICON = 1;
        public static final int POSITION_TEXT_PRIMARY = 2;
        public static final int POSITION_TEXT_SECONDARY = 3;
        public static final int POSITION_TEXT_SECONDARY_2 = 4;
        public static final int POSITION_END_ICON = 5;
        public static final int POSITION_END_TEXT_TOP = 6;
        public static final int POSITION_END_TEXT_BOTTOM = 7;

        @Position
        public int position = POSITION_UNSPECIFIED;

        public LayoutParams(@NonNull Context c, AttributeSet attrs) {
            super(c, attrs);

            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ListItemLayout_Layout);
            //noinspection WrongConstant
            position = a.getInt(R.styleable.ListItemLayout_Layout_layout_position,
                    POSITION_UNSPECIFIED);
            a.recycle();
        }
        public LayoutParams(int width, int height) {
            super(width, height);
        }
        public LayoutParams(int width, int height, int position) {
            super(width, height);
            this.position = position;
        }
        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
        }
        @TargetApi(Build.VERSION_CODES.KITKAT)
        public LayoutParams(@NonNull LayoutParams source) {
            super(source);

            position = source.position;
        }
    }
}
