package org.devbid.infrastructure.config;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

public class PrettySqlFormat implements MessageFormattingStrategy {
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        if (sql == null || sql.trim().isEmpty()) {
            return "";
        }

        // 호출한 메서드 정보 추출
        String methodInfo = getCallingMethodInfo();

        // 테이블 별칭 제거
        sql = sql.replaceAll("u[0-9]+_[0-9]+\\.", "");
        sql = sql.replaceAll("(\\b\\w+\\b) u[0-9]+_[0-9]+", "$1");

        // SQL 포맷팅
        sql = formatSql(sql);

        /*
        grap setting
        (──.*SQL DEBUG.*──|^────────────────────────────────────────────$)
        (?<=Method : )\S+\.\w+\(\)
        WHERE\s+username\s*=\s*'[^']*'
        * */
        return String.format("""
                
                ── SQL DEBUG ───────────────────────────────
                Method : %s
                Time   : %d ms
                SQL    :
                %s
                ────────────────────────────────────────────
                """, methodInfo, elapsed, sql.trim());
    }

    private String getCallingMethodInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        // 스택 트레이스에서 org.devbid 패키지의 실제 비즈니스 로직 찾기
        for (int i = stackTrace.length - 1; i >= 0; i--) {
            StackTraceElement element = stackTrace[i];
            String className = element.getClassName();
            String methodName = element.getMethodName();

            // org.devbid 패키지의 클래스 중에서 config가 아닌 것들 찾기 (역순으로)
            if (className.startsWith("org.devbid") &&
                !className.contains("config") &&
                !className.contains("PrettySqlFormat")) {

                return extractSimpleMethodName(className, methodName);
            }
        }

        // 역순으로 못 찾으면 정순으로 다시 찾기
        /*for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            String methodName = element.getMethodName();

            if (className.startsWith("org.devbid") &&
                !className.contains("config") &&
                !className.contains("PrettySqlFormat") &&
                !methodName.equals("getStackTrace") &&
                !methodName.equals("getCallingMethodInfo")) {

                return extractSimpleMethodName(className, methodName);
            }
        }*/

        return "Unknown.method()";
    }

    private String extractSimpleMethodName(String className, String methodName) {
        String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
        // CGLIB 프록시 처리
        if (simpleClassName.contains("$$")) {
            simpleClassName = simpleClassName.substring(0, simpleClassName.indexOf("$$"));
        }
        return simpleClassName + "." + methodName + "()";
    }

    private String formatSql(String sql) {
        return sql.replaceAll("(?i)select", "\nSELECT")
                .replaceAll(",(?![^()]*\\))", ",\n    ")
                .replaceAll("(?i)\\bfrom\\b", "\nFROM")
                .replaceAll("(?i)\\bwhere\\b", "\nWHERE")
                .replaceAll("(?i)\\band\\b", "\n  AND")
                .replaceAll("(?i)\\bor\\b", "\n  OR")
                .replaceAll("(?i)\\bvalues\\b", "\nVALUES")
                .replaceAll("(?i)\\binsert\\s+into\\b", "\nINSERT INTO")
                .replaceAll("(?i)\\bupdate\\b", "\nUPDATE")
                .replaceAll("(?i)\\bset\\b", "\nSET")
                .replaceAll("(?i)\\bdelete\\s+from\\b", "\nDELETE FROM")
                .replaceAll("(?i)\\border\\s+by\\b", "\nORDER BY")
                .replaceAll("(?i)\\bgroup\\s+by\\b", "\nGROUP BY")
                .replaceAll("(?i)\\bhaving\\b", "\nHAVING")
                .replaceAll("(?i)\\blimit\\b", "\nLIMIT")
                .replaceAll("(?i)\\boffset\\b", "\nOFFSET")
                .replaceAll("(?i)\\bjoin\\b", "\nJOIN")
                .replaceAll("(?i)\\bleft\\s+join\\b", "\nLEFT JOIN")
                .replaceAll("(?i)\\bright\\s+join\\b", "\nRIGHT JOIN")
                .replaceAll("(?i)\\binner\\s+join\\b", "\nINNER JOIN")
                .replaceAll("(?i)\\bon\\b", "\n  ON");
    }
}