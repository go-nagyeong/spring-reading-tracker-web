import {
    ClassicEditor,
    AccessibilityHelp,
    Alignment,
    AutoLink,
    Autosave,
    BlockQuote,
    Bold,
    Essentials,
    Heading,
    HorizontalLine,
    Indent,
    IndentBlock,
    Italic,
    Link,
    Paragraph,
    Strikethrough,
    Underline,
    Undo
} from 'ckeditor5';

import translations from 'ckeditor5/translations/ko.js';

const editorConfig = {
    toolbar: {
        items: [
            'undo',
            'redo',
            '|',
            'heading',
            '|',
            'bold',
            'italic',
            'underline',
            'strikethrough',
            '|',
            'horizontalLine',
            'link',
            'blockQuote',
            '|',
            'alignment',
            '|',
            'indent',
            'outdent',
            '|',
            'accessibilityHelp'
        ],
        shouldNotGroupWhenFull: false
    },
    plugins: [
        AccessibilityHelp,
        Alignment,
        AutoLink,
        Autosave,
        BlockQuote,
        Bold,
        Essentials,
        Heading,
        HorizontalLine,
        Indent,
        IndentBlock,
        Italic,
        Link,
        Paragraph,
        Strikethrough,
        Underline,
        Undo
    ],
    heading: {
        options: [
            {
                model: 'paragraph',
                title: 'Paragraph',
                class: 'ck-heading_paragraph'
            },
            {
                model: 'heading1',
                view: 'h1',
                title: 'Heading 1',
                class: 'ck-heading_heading1'
            },
            {
                model: 'heading2',
                view: 'h2',
                title: 'Heading 2',
                class: 'ck-heading_heading2'
            },
            {
                model: 'heading3',
                view: 'h3',
                title: 'Heading 3',
                class: 'ck-heading_heading3'
            },
            {
                model: 'heading4',
                view: 'h4',
                title: 'Heading 4',
                class: 'ck-heading_heading4'
            },
            {
                model: 'heading5',
                view: 'h5',
                title: 'Heading 5',
                class: 'ck-heading_heading5'
            },
            {
                model: 'heading6',
                view: 'h6',
                title: 'Heading 6',
                class: 'ck-heading_heading6'
            }
        ]
    },
    initialData: '',
    language: 'ko',
    link: {
        addTargetToExternalLinks: true,
        defaultProtocol: 'https://',
        decorators: {
            toggleDownloadable: {
                mode: 'manual',
                label: 'Downloadable',
                attributes: {
                    download: 'file'
                }
            }
        }
    },
    placeholder: '',
    translations: [translations]
};

window.editorInstance = null;

window.initializeEditor = () => {
    if (window.editorInstance) {
        window.editorInstance.destroy();
    }

    ClassicEditor.create(document.querySelector('#editor'), editorConfig)
        .then(editor => {
            editorInstance = editor;
        })
        .catch(error => {
            console.error('Error initializing editor:', error);
        });
};