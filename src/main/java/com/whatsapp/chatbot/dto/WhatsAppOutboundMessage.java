package com.whatsapp.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for sending WhatsApp messages
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppOutboundMessage {
    private String messaging_product = "whatsapp";
    private String recipient_type = "individual";
    private String to;
    private String type;
    private Text text;
    private Interactive interactive;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Text {
        private boolean preview_url = false;
        private String body;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Interactive {
        private String type;
        private Header header;
        private Body body;
        private Footer footer;
        private Action action;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Header {
            private String type;
            private String text;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Body {
            private String text;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Footer {
            private String text;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Action {
            private Button[] buttons;
            private String button;
            private Section[] sections;

            @Data
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Button {
                private String type = "reply";
                private Reply reply;

                @Data
                @Builder
                @NoArgsConstructor
                @AllArgsConstructor
                public static class Reply {
                    private String id;
                    private String title;
                }
            }

            @Data
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Section {
                private String title;
                private Row[] rows;

                @Data
                @Builder
                @NoArgsConstructor
                @AllArgsConstructor
                public static class Row {
                    private String id;
                    private String title;
                    private String description;
                }
            }
        }
    }
}
