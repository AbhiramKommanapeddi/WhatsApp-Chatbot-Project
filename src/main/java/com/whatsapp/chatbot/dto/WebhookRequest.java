package com.whatsapp.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for WhatsApp Business API webhook requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookRequest {
    private String object;
    private Entry[] entry;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entry {
        private String id;
        private Change[] changes;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Change {
            private String field;
            private Value value;

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Value {
                private String messaging_product;
                private Metadata metadata;
                private Contact[] contacts;
                private Message[] messages;
                private Status[] statuses;

                @Data
                @NoArgsConstructor
                @AllArgsConstructor
                public static class Metadata {
                    private String display_phone_number;
                    private String phone_number_id;
                }

                @Data
                @NoArgsConstructor
                @AllArgsConstructor
                public static class Contact {
                    private String wa_id;
                    private Profile profile;

                    @Data
                    @NoArgsConstructor
                    @AllArgsConstructor
                    public static class Profile {
                        private String name;
                    }
                }

                @Data
                @NoArgsConstructor
                @AllArgsConstructor
                public static class Message {
                    private String id;
                    private String from;
                    private String timestamp;
                    private String type;
                    private Text text;
                    private Interactive interactive;

                    @Data
                    @NoArgsConstructor
                    @AllArgsConstructor
                    public static class Text {
                        private String body;
                    }

                    @Data
                    @NoArgsConstructor
                    @AllArgsConstructor
                    public static class Interactive {
                        private String type;
                        private ButtonReply button_reply;
                        private ListReply list_reply;

                        @Data
                        @NoArgsConstructor
                        @AllArgsConstructor
                        public static class ButtonReply {
                            private String id;
                            private String title;
                        }

                        @Data
                        @NoArgsConstructor
                        @AllArgsConstructor
                        public static class ListReply {
                            private String id;
                            private String title;
                            private String description;
                        }
                    }
                }

                @Data
                @NoArgsConstructor
                @AllArgsConstructor
                public static class Status {
                    private String id;
                    private String status;
                    private String timestamp;
                    private String recipient_id;
                }
            }
        }
    }
}
