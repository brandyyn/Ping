package mega.ping.data;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true, chain = false)
public enum PingAction {
    GOTO("GOTO"), LOOK("Look"), ALERT("Alert"), MINE("Mine");

    private final String internalName = name().toLowerCase();
    private final String unlocalizedName = "megaping.action." + internalName;
    private final String readableName;

    @Override
    public String toString() {
        return readableName;
    }
}
